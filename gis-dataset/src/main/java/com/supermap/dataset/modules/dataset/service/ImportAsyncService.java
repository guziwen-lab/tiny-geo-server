package com.supermap.dataset.modules.dataset.service;

import com.supermap.gdal.GdalTool;
import com.supermap.core.common.util.CollectionUtils;
import com.supermap.core.common.util.FileNameUtils;
import com.supermap.gdal.config.GdalProperties;
import com.supermap.gis.enums.GeomType;
import com.supermap.dataset.modules.dataset.dto.GdbLayerSource;
import com.supermap.gdal.info.LayerMeta;
import com.supermap.dataset.modules.dataset.entity.DatasetEntity;
import com.supermap.gis.service.GeometryService;
import com.supermap.gdal.encoding.ShapeEncodingDetector;
import com.supermap.gis.util.TableNameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportAsyncService {

    private final GeometryService geometryService;
    private final ImportStatusUpdater importStatusUpdater;
    private final GdalProperties gdalProperties;
    private final GdalTool gdalTool;

    @Async("importTaskExecutor")
    public void importLayerAsync(DatasetEntity entity, String sourcePath, String exportLayerName, boolean isAppend) {
        String tableName = entity.getTableName();
        try {
            // 查询图层元数据
            LayerMeta meta = gdalTool.queryLayerMeta(sourcePath, exportLayerName);

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
            gdalTool.importLayer(sourcePath, tableName, exportLayerName, isAppend, entity.getGeomType(), null, null);

            // 检查几何类型（优先以 PostgreSQL 实际存储的几何类型为准）
            GeomType geomType = geometryService.resolveActualGeomType(
                    gdalProperties.getSchema() + "." + tableName, meta.geomType());
            if (geomType == null) {
                throw new RuntimeException("几何类型不支持: " + meta.geomType());
            }

            // 创建空间索引
            geometryService.createGistIndex(gdalProperties.getSchema(), tableName);

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

    @Async("importTaskExecutor")
    public void importShpLayersAsync(DatasetEntity entity,
                                     List<String> paths,
                                     Integer srid,
                                     boolean isAppend) {
        List<GdbLayerSource> sources = new ArrayList<>();
        for (String path : paths) {
            String ln = FileNameUtils.getFileNameWithoutExtension(path);
            String confirmEncoding = gdalTool.detectEncoding(path, ln);

            GdbLayerSource gdbLayerSource = new GdbLayerSource(path, ln, confirmEncoding);
            sources.add(gdbLayerSource);
        }

        importLayersAsync(entity, sources, srid, isAppend);
    }

    @Async("importTaskExecutor")
    public void importGdbLayersAsync(DatasetEntity entity,
                                     List<String> paths,
                                     Integer srid,
                                     boolean isAppend) {
        List<GdbLayerSource> sources = new ArrayList<>();
        for (String gdbPath : paths) {
            List<String> layerNames = gdalTool.listGdbLayers(gdbPath);
            if (layerNames.isEmpty())
                throw new IllegalArgumentException("GDB中未找到任何图层: " + gdbPath);
            if (!layerNames.contains(entity.getLayerName()))
                throw new IllegalArgumentException("图层不存在: " + entity.getLayerName() + ", GDB=" + gdbPath);

            GdbLayerSource gdbLayerSource = new GdbLayerSource(gdbPath, entity.getLayerName());
            sources.add(gdbLayerSource);
        }

        importLayersAsync(entity, sources, srid, isAppend);
    }

    /**
     * 顺序导入同一投影组中的多个 GDB 图层。必须在同一个异步任务中顺序执行，
     * 否则“首个建表”与后续“追加”会产生竞争。
     */
    @Async("importTaskExecutor")
    public void importLayersAsync(DatasetEntity entity,
                                  List<GdbLayerSource> sources,
                                  Integer srid,
                                  boolean isAppend) {
        String tableName = entity.getTableName();
        try {
            if (CollectionUtils.isEmpty(sources)) {
                throw new IllegalArgumentException("导入图层不能为空");
            }

            LayerMeta first = gdalTool.queryLayerMeta(sources.get(0).getPath(), sources.get(0).getLayerName());
            long featureCount = 0;
            for (int i = 0; i < sources.size(); i++) {
                GdbLayerSource source = sources.get(i);
                LayerMeta meta = gdalTool.queryLayerMeta(source.getPath(), source.getLayerName());
                if (srid == null && !Objects.equals(first.srid(), meta.srid())) {
                    throw new RuntimeException("批量导入分组内 SRID 不一致: " + first.srid() + " / " + meta.srid());
                }
                checkGeomTypeCompatible(GeomType.ofOgr2ogrCode(first.geomType()), meta.geomType());
                gdalTool.importLayer(source.getPath(), tableName, source.getLayerName(), isAppend || i > 0,
                        i == 0 ? null : GeomType.ofOgr2ogrCode(first.geomType()), srid, source.getEncoding());
                featureCount += meta.featureCount();
            }

            GeomType geomType = geometryService.resolveActualGeomType(
                    TableNameUtils.getTableNameWithSchema(gdalProperties.getSchema(), tableName), first.geomType());
            if (geomType == null) {
                throw new RuntimeException("几何类型不支持: " + first.geomType());
            }
            geometryService.createGistIndex(gdalProperties.getSchema(), tableName);
            importStatusUpdater.markSuccess(entity.getId(), geomType, srid == null ? first.srid() : srid, featureCount);
        } catch (Exception e) {
            log.error("批量导入失败, datasetId={}, table={}", entity.getId(), tableName, e);
            try {
                geometryService.dropTableIfExists(TableNameUtils.getTableNameWithSchema(gdalProperties.getSchema(), tableName));
            } catch (Exception dropEx) {
                log.error("清理失败表失败: {}", tableName, dropEx);
            }
            importStatusUpdater.markFailed(entity.getId(), e.getMessage());
        }
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

}
