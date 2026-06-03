package com.supermap.modules.analyze.service.impl;

import com.supermap.analyze.security.SqlInjectionCheck;
import com.supermap.common.util.StringUtils;
import com.supermap.dao.GeometryDao;
import com.supermap.enumeration.DatasetType;
import com.supermap.enumeration.GeomType;
import com.supermap.modules.analyze.entity.DatasetEntity;
import com.supermap.modules.analyze.service.DatasetService;
import com.supermap.modules.analyze.service.UploadService;
import com.supermap.util.DsSnGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author gzw
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final DsSnGenerator dsSnGenerator;
    private final DatasetService datasetService;
    private final GeometryDao geometryDao;

    @Value("${gdal.pgConn}")
    private String pgConn;

    private static final String LAYER_PATTERN = "^Layer:\\s+(.+?)\\s*(?:\\(|$)";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long importShp(String shpPath) {
        DatasetEntity entity = null;
        try {
            String layerName = getFileNameWithoutExtension(shpPath);

            entity = importLayer(shpPath, layerName, DatasetType.SHP);
            entity.setDatasetName(layerName);
            entity.setLayerName(layerName);
            datasetService.save(entity);
            return entity.getId();
        } catch (Exception e) {
            if (entity != null) {
                dropTableAfterTransaction(entity.getTableName());
            }
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Long> importGdb(String gdbPath, String layerName) {
        List<String> layerNames = listGdbLayers(gdbPath);
        if (layerNames.isEmpty()) {
            throw new RuntimeException("GDB中未找到任何图层: " + gdbPath);
        }

        List<DatasetEntity> datasetEntities = new ArrayList<>();
        if (StringUtils.isEmpty(layerName)) {
            for (String ln : layerNames) {
                DatasetEntity entity = importLayer(gdbPath, ln, DatasetType.GDB);
                datasetEntities.add(entity);
            }
        } else {
            if (!layerNames.contains(layerName)) {
                throw new RuntimeException("图层不存在: " + layerName);
            }

            DatasetEntity entity = importLayer(gdbPath, layerName, DatasetType.GDB);
            datasetEntities.add(entity);
        }

        try {
            datasetService.saveBatch(datasetEntities);
        } catch (Exception e) {
            for (DatasetEntity datasetEntity : datasetEntities) {
                dropTableAfterTransaction(datasetEntity.getTableName());
            }
            throw e;
        }

        return datasetEntities.stream().map(DatasetEntity::getId).collect(Collectors.toList());
    }

    private DatasetEntity importLayer(String sourcePath, String layerName, DatasetType datasetType) {
        String tableName = "ds_" + dsSnGenerator.generate();

        String exportLayerName = Objects.equals(datasetType, DatasetType.GDB) ? layerName : null;

        execOgr2ogr(sourcePath, tableName, exportLayerName);

        TableMeta metaBefore = queryLayerMeta(sourcePath, exportLayerName);
//        TableMeta metaAfter = queryTableMeta(tableName);
//        checkMateData(metaBefore, metaAfter, tableName);

        // 修复几何类型
        GeomType geomType = GeomType.of(metaBefore.geomType);
        if (geomType == null)
            throw new RuntimeException("几何类型不支持: " + metaBefore.geomType);

        long invalidFeatureCount = switch (geomType) {
            case MULTI_POLYGON -> geometryDao.fixGeometryTypeByMultipolygon(tableName);
            case POINT -> geometryDao.fixGeometryTypeByPoint(tableName);
            case MULTI_LINE_STRING -> geometryDao.fixGeometryTypeByMultiLineString(tableName);
        };

        geometryDao.createGistIndex(tableName);

        DatasetEntity entity = new DatasetEntity();
        entity.setDatasetName(layerName);
        entity.setDatasetType(datasetType.name());
        entity.setSourceFile(sourcePath);
        entity.setLayerName(layerName);
        entity.setTableName(tableName);
        entity.setGeomType(metaBefore.geomType);
        entity.setSrid(metaBefore.srid);
        entity.setFeatureCount(metaBefore.featureCount);
        entity.setInvalidFeatureCount(invalidFeatureCount);
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    private void checkMateData(TableMeta before, TableMeta after, String tableName) {
        if (!Objects.equals(before.srid, after.srid)
                || !Objects.equals(before.featureCount, after.featureCount)) {
            dropTableAfterTransaction(tableName);
            throw new RuntimeException("数据格式校验不正确，导入前后读取的元数据不一致");
        }
    }

    /**
     * 执行 ogr2ogr 将数据导入 PostgreSQL
     *
     * @param sourcePath 源文件路径（shp 或 gdb 目录）
     * @param tableName  目标表名
     * @param layerName  指定图层名（GDB 时需要，SHP 传 null）
     */
    private void execOgr2ogr(String sourcePath, String tableName, String layerName) {
        List<String> cmd = new ArrayList<>();
        cmd.add("ogr2ogr");
        cmd.add("-f");
        cmd.add("PostgreSQL");
        cmd.add("-overwrite");
        cmd.add(pgConn);
        cmd.add(sourcePath);
        cmd.add("-nln");
        cmd.add(tableName);
        if (layerName != null) {
            cmd.add(layerName);
        }
        cmd.add("-lco");
        cmd.add("GEOMETRY_NAME=geom");
        // 不增加索引，后续手动增加
        cmd.add("-lco");
        cmd.add("SPATIAL_INDEX=NONE");

        if (sourcePath.toLowerCase().endsWith(".shp")) {
            cmd.add("--config");
            cmd.add("SHAPE_ENCODING");
            cmd.add("GBK");
        }

        log.info("执行导入命令: {}", String.join(" ", cmd));

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            int code = process.waitFor();
            if (code != 0) {
                log.error("ogr2ogr 导入失败, exitCode={}, output={}", code, output);
                throw new RuntimeException("ogr2ogr 导入失败(exitCode=" + code + "): " + output);
            }
            log.info("导入成功, table={}, output={}", tableName, output);
        } catch (IOException e) {
            throw new RuntimeException("执行 ogr2ogr 失败，请确认已安装 GDAL", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ogr2ogr 导入过程被中断", e);
        }
    }

    /**
     * 使用 ogrinfo 列出 GDB 中的所有图层名
     */
    private List<String> listGdbLayers(String gdbPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ogrinfo", "-so", gdbPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            List<String> layers = new ArrayList<>();
            // ogrinfo -so /Users/guziwen/Downloads/test.gdb
            // INFO: Open of `/Users/guziwen/Downloads/test.gdb'
            //      using driver `OpenFileGDB' successful.
            //Layer: sys_administrative_division_code_with_geom (Multi Polygon)
            Pattern pattern = Pattern.compile(LAYER_PATTERN);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        layers.add(matcher.group(1));
                    }
                }
            }

            int code = process.waitFor();
            if (code != 0) {
                log.error("执行 ogrinfo 失败, exitCode={}", code);
                throw new RuntimeException("执行 ogrinfo 失败(exitCode=" + code + ")");
            }
            log.info("GDB图层列表: {}", layers);
            return layers;
        } catch (IOException e) {
            throw new RuntimeException("执行 ogrinfo 失败，请确认已安装 GDAL", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ogrinfo 过程被中断", e);
        }
    }

    /**
     * 导入后查询表的元数据（几何类型、SRID、要素数量）
     */
    private TableMeta queryTableMeta(String tableName) {
        SqlInjectionCheck.checkTableName(tableName);

        TableMeta meta = new TableMeta();
        meta.geomType = geometryDao.getGeometryType(tableName);
        meta.srid = geometryDao.getSrid(tableName);

        geometryDao.analyzeTable(tableName);
        meta.featureCount = geometryDao.getFeatureCount(tableName);

        return meta;
    }

    /**
     * 导入前查询shp/gdb的元数据（几何类型、SRID、要素数量）
     */
    private TableMeta queryLayerMeta(String path, String layerName) {
        TableMeta meta = new TableMeta();

        try {
            ProcessBuilder pb = new ProcessBuilder("ogrinfo", "-so", path, layerName);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            Pattern geomPattern = Pattern.compile("^Geometry:\\s+(.+)$");
            Pattern countPattern = Pattern.compile("^Feature Count:\\s+(\\d+)$");
            Pattern epsgPattern = Pattern.compile("ID\\[\"EPSG\",(\\d+)]");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher geomMatcher = geomPattern.matcher(line);
                    if (geomMatcher.find()) {
                        meta.geomType = geomMatcher.group(1).trim();
                        continue;
                    }

                    Matcher countMatcher = countPattern.matcher(line);
                    if (countMatcher.find()) {
                        meta.featureCount = Long.parseLong(countMatcher.group(1));
                        continue;
                    }

                    Matcher epsgMatcher = epsgPattern.matcher(line);
                    if (epsgMatcher.find()) {
                        meta.srid = Integer.parseInt(epsgMatcher.group(1));
                    }
                }
            }

            int code = process.waitFor();
            if (code != 0) {
                log.error("执行 ogrinfo 获取 LayerMeta 失败, exitCode={}", code);
                throw new RuntimeException("执行 ogrinfo 获取 LayerMeta 失败 (exitCode=" + code + ")");
            }
        } catch (IOException e) {
            throw new RuntimeException("执行 ogrinfo 失败，请确认已安装 GDAL", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ogrinfo 过程被中断", e);
        }

        return meta;
    }

    /**
     * 注册事务完成后的回调，在外层事务提交/回滚后再执行 DROP TABLE，
     * 避免新事务与当前事务的数据库锁冲突导致死锁。
     */
    private void dropTableAfterTransaction(String tableName) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                try {
                    geometryDao.dropTable(tableName);
                } catch (Exception e) {
                    log.error("事务后清理表失败: {}", tableName, e);
                }
            }
        });
    }

    private static String getFileNameWithoutExtension(String path) {
        String name = path;
        int sep = name.lastIndexOf('/');
        if (sep < 0) {
            sep = name.lastIndexOf('\\');
        }
        if (sep >= 0) {
            name = name.substring(sep + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            name = name.substring(0, dot);
        }
        return name;
    }

    /**
     * 表元数据内部类
     */
    private static class TableMeta {

        String geomType;
        Integer srid;
        Long featureCount;

    }

}
