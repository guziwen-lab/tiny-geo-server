package com.supermap.modules.dataset.service;

import com.supermap.config.DatasetProperties;
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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportAsyncService {

    private final GeometryService geometryService;
    private final ImportStatusUpdater importStatusUpdater;
    private final DatasetProperties datasetProperties;

    @Async("importTaskExecutor")
    public void importLayerAsync(DatasetEntity entity, String sourcePath, String exportLayerName, boolean isAppend) {
        String tableName = entity.getTableName();
        try {
            // 查询图层元数据
            LayerMeta meta = queryLayerMeta(sourcePath, exportLayerName);

            // 如果是追加导入，先校验 srid 和几何类型，避免脏数据写入原表；featureCount累加
            long featureCount = meta.featureCount();
            if (isAppend) {
                Integer srid = entity.getSrid();
                if (!Objects.equals(srid, meta.srid())) {
                    throw new RuntimeException("SRID 不匹配: 原SRID=" + srid + ", 新SRID=" + meta.srid());
                }

                checkGeomTypeCompatible(entity.getGeomType(), meta.geomType());

                featureCount += entity.getFeatureCount();
            }

            // 执行 ogr2ogr 导入
            execOgr2ogr(sourcePath, tableName, exportLayerName, isAppend, entity.getGeomType());

            // 检查几何类型（优先以 PostgreSQL 实际存储的几何类型为准）
            GeomType geomType = geometryService.resolveActualGeomType(
                    datasetProperties.getSchema() + "." + tableName, meta.geomType());
            if (geomType == null) {
                throw new RuntimeException("几何类型不支持: " + meta.geomType());
            }

            // 创建空间索引
            geometryService.createGistIndex(datasetProperties.getSchema(), tableName);

            // 更新状态为成功
            importStatusUpdater.markSuccess(
                    entity.getId(),
                    geomType,
                    meta.srid(),
                    featureCount
            );
        } catch (Exception e) {
            log.error("数据集导入失败, datasetId={}, table={}", entity.getId(), tableName, e);
            // 清理已创建的表
            if (!isAppend) {
                try {
                    geometryService.dropTableIfExists(tableName);
                } catch (Exception dropEx) {
                    log.error("清理失败表失败: {}", tableName, dropEx);
                }
            }
            importStatusUpdater.markFailed(entity.getId(), e.getMessage());
        }
    }

    /**
     * 顺序导入同一投影组中的多个 GDB 图层。必须在同一个异步任务中顺序执行，
     * 否则“首个建表”与后续“追加”会产生竞争。
     */
    @Async("importTaskExecutor")
    public void importLayersAsync(DatasetEntity entity, List<GdbLayerSource> sources) {
        String tableName = entity.getTableName();
        try {
            if (sources.isEmpty()) {
                throw new IllegalArgumentException("导入图层不能为空");
            }

            LayerMeta first = inspectLayer(sources.get(0).path(), sources.get(0).layerName());
            long featureCount = 0;
            for (int i = 0; i < sources.size(); i++) {
                GdbLayerSource source = sources.get(i);
                LayerMeta meta = inspectLayer(source.path(), source.layerName());
                if (!Objects.equals(first.srid(), meta.srid())) {
                    throw new RuntimeException("批量导入分组内 SRID 不一致: " + first.srid() + " / " + meta.srid());
                }
                checkGeomTypeCompatible(GeomType.ofOgr2ogrCode(first.geomType()), meta.geomType());
                execOgr2ogr(source.path(), tableName, source.layerName(), i > 0,
                        i == 0 ? null : GeomType.ofOgr2ogrCode(first.geomType()));
                featureCount += meta.featureCount();
            }

            GeomType geomType = geometryService.resolveActualGeomType(
                    datasetProperties.getSchema() + "." + tableName, first.geomType());
            if (geomType == null) {
                throw new RuntimeException("几何类型不支持: " + first.geomType());
            }
            geometryService.createGistIndex(datasetProperties.getSchema(), tableName);
            importStatusUpdater.markSuccess(entity.getId(), geomType, first.srid(), featureCount);
        } catch (Exception e) {
            log.error("批量 GDB 导入失败, datasetId={}, table={}", entity.getId(), tableName, e);
            try {
                geometryService.dropTableIfExists(tableName);
            } catch (Exception dropEx) {
                log.error("清理失败表失败: {}", tableName, dropEx);
            }
            importStatusUpdater.markFailed(entity.getId(), e.getMessage());
        }
    }

    /** 查询 GDB 图层元数据，供批量导入在建表前按坐标系分组。 */
    public LayerMeta inspectLayer(String path, String layerName) {
        return queryLayerMeta(path, layerName);
    }

    public record GdbLayerSource(String path, String layerName) {
    }

    public record LayerMeta(String geomType, Integer srid, long featureCount) {
    }

    /**
     * 追加导入前校验源数据与目标表几何类型是否兼容：
     * 必须为同一几何族，且目标为非 Multi 类型时源数据不能是 Multi 类型（无法降级）
     */
    private static void checkGeomTypeCompatible(GeomType tableGeomType, String sourceGeomTypeName) {
        GeomType sourceGeomType = GeomType.ofOgr2ogrCode(sourceGeomTypeName);
        if (sourceGeomType == null) {
            throw new RuntimeException("几何类型不支持: " + sourceGeomTypeName);
        }

        boolean sameFamily = sourceGeomType.getCollectionExtractType() == tableGeomType.getCollectionExtractType();
        if (!sameFamily || (!tableGeomType.isMulti() && sourceGeomType.isMulti())) {
            throw new RuntimeException("几何类型不匹配: 原类型=" + tableGeomType.getGeometryName()
                    + ", 新类型=" + sourceGeomTypeName);
        }
    }

    /**
     * 执行 ogr2ogr 将数据导入 PostgreSQL
     */
    private void execOgr2ogr(String sourcePath, String tableName, String layerName, boolean isAppend, GeomType targetGeomType) {
        List<String> cmd = new ArrayList<>();
        cmd.add("ogr2ogr");
        cmd.add("-f");
        cmd.add("PostgreSQL");
        if (isAppend) {
            cmd.add("-append");
            cmd.add("-addfields");
        } else {
            cmd.add("-overwrite");
        }
        cmd.add(datasetProperties.getPgConnect());
        cmd.add(sourcePath);
        cmd.add("-nln");

        if (isAppend) {
            cmd.add(datasetProperties.getSchema() + "." + tableName);
        } else {
            cmd.add(tableName);

            // -lco 为图层创建选项，仅在新建表时生效，追加模式下无需传递
            cmd.add("-lco");
            cmd.add("GEOMETRY_NAME=geom");
            cmd.add("-lco");
            cmd.add("SPATIAL_INDEX=NONE");
            cmd.add("-lco");
            cmd.add("SCHEMA=" + datasetProperties.getSchema());
        }

        if (layerName != null) {
            cmd.add(layerName);
        }

        if (sourcePath.toLowerCase().endsWith(".shp")) {
            cmd.add("--config");
            cmd.add("SHAPE_ENCODING");
            cmd.add("GBK");
        }

        if (isAppend) {
            // 追加时以目标表的几何类型为准：目标为 Multi 类型则提升源数据，
            // 目标为非 Multi 类型则不提升（预校验已保证源数据不含 Multi 类型）
            if (targetGeomType != null && targetGeomType.isMulti()) {
                cmd.add("-nlt");
                cmd.add("PROMOTE_TO_MULTI");
            }
        } else if (sourcePath.toLowerCase().endsWith(".shp")) {
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
    private LayerMeta queryLayerMeta(String path, String layerName) {
        String geomType = null;
        Integer srid = null;
        long featureCount = 0;

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
            Pattern epsgPattern = Pattern.compile("(?:ID\\[\"EPSG\",|AUTHORITY\\[\"EPSG\",\")(\\d+)\"?]");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher geomMatcher = geomPattern.matcher(line);
                    if (geomMatcher.find()) {
                        geomType = geomMatcher.group(1).trim();
                        continue;
                    }

                    Matcher countMatcher = countPattern.matcher(line);
                    if (countMatcher.find()) {
                        featureCount = Long.parseLong(countMatcher.group(1));
                        continue;
                    }

                    Matcher epsgMatcher = epsgPattern.matcher(line);
                    if (epsgMatcher.find()) {
                        srid = Integer.parseInt(epsgMatcher.group(1));
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

        return new LayerMeta(geomType, srid, featureCount);
    }

}
