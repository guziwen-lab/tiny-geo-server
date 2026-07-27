package com.supermap.modules.dataset.service;

import com.supermap.config.DatasetProperties;
import com.supermap.dao.GeometryDao;
import com.supermap.enums.GeomType;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.service.GeometryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportAsyncService {

    private final GeometryDao geometryDao;
    private final GeometryService geometryService;
    private final ImportStatusUpdater importStatusUpdater;
    private final DatasetProperties datasetProperties;

    @Async("importTaskExecutor")
    public void importLayerAsync(DatasetEntity entity, String sourcePath, String exportLayerName) {
        String tableName = entity.getTableName();
        try {
            // 1. 执行 ogr2ogr 导入
            execOgr2ogr(sourcePath, tableName, exportLayerName);

            // 2. 查询图层元数据
            TableMeta meta = queryLayerMeta(sourcePath, exportLayerName);

            // 3. 检查几何类型（优先以 PostgreSQL 实际存储的几何类型为准）
            GeomType geomType = geometryService.resolveActualGeomType(
                    datasetProperties.getSchema() + "." + tableName, meta.geomType);
            if (geomType == null) {
                throw new RuntimeException("几何类型不支持: " + meta.geomType);
            }

            // 4. 创建空间索引
            geometryDao.createGistIndex(datasetProperties.getSchema(), tableName);

            // 5. 更新状态为成功
            importStatusUpdater.markSuccess(
                    entity.getId(),
                    geomType,
                    meta.srid,
                    meta.featureCount
            );
        } catch (Exception e) {
            log.error("数据集导入失败, datasetId={}, table={}", entity.getId(), tableName, e);
            // 清理已创建的表
            try {
                geometryDao.dropTableIfExists(tableName);
            } catch (Exception dropEx) {
                log.error("清理失败表失败: {}", tableName, dropEx);
            }
            importStatusUpdater.markFailed(entity.getId(), e.getMessage());
        }
    }

    /**
     * 执行 ogr2ogr 将数据导入 PostgreSQL
     */
    private void execOgr2ogr(String sourcePath, String tableName, String layerName) {
        List<String> cmd = new ArrayList<>();
        cmd.add("ogr2ogr");
        cmd.add("-f");
        cmd.add("PostgreSQL");
        cmd.add("-overwrite");
        cmd.add(datasetProperties.getPgConnect());
        cmd.add(sourcePath);
        cmd.add("-nln");
        cmd.add(tableName);
        if (layerName != null) {
            cmd.add(layerName);
        }
        cmd.add("-lco");
        cmd.add("GEOMETRY_NAME=geom");
        cmd.add("-lco");
        cmd.add("SPATIAL_INDEX=NONE");
        cmd.add("-lco");
        cmd.add("SCHEMA=" + datasetProperties.getSchema());

        if (sourcePath.toLowerCase().endsWith(".shp")) {
            cmd.add("--config");
            cmd.add("SHAPE_ENCODING");
            cmd.add("GBK");
            // Shapefile 的 Polygon 图层可能实际包含 MultiPolygon，
            // 强制提升为 Multi 类型避免 PostgreSQL COPY 阶段几何类型不匹配
            cmd.add("-nlt");
            cmd.add("PROMOTE_TO_MULTI");
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
     * 导入前查询 shp/gdb 的元数据（几何类型、SRID、要素数量）
     */
    private TableMeta queryLayerMeta(String path, String layerName) {
        TableMeta meta = new TableMeta();

        try {
            List<String> cmd = new ArrayList<>();
            cmd.add("ogrinfo");
            cmd.add("-so");
            cmd.add(path);
            if (layerName != null) {
                cmd.add(layerName);
            }

            log.info("执行查询元数据命令: {}", String.join(" ", cmd));

            ProcessBuilder pb = new ProcessBuilder(cmd);
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

    private static class TableMeta {
        String geomType;
        Integer srid;
        Long featureCount;
    }

}
