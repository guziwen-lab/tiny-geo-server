package com.supermap.modules.dataset.service.impl;

import com.supermap.GdalTool;
import com.supermap.common.util.FileNameUtils;
import com.supermap.common.util.JSON;
import com.supermap.common.util.StringUtils;
import com.supermap.config.DatasetProperties;
import com.supermap.enums.DatasetType;
import com.supermap.enums.UploadStatus;
import com.supermap.modules.dataset.dao.FeatureDao;
import com.supermap.modules.dataset.dto.*;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.dataset.entity.FeatureEntity;
import com.supermap.modules.dataset.service.ImportAsyncService;
import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.modules.dataset.service.ImportService;
import com.supermap.util.DatasetTableNameGenerator;
import com.supermap.util.IdentifierGeneratorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gzw
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final DatasetTableNameGenerator datasetTableNameGenerator;
    private final DatasetService datasetService;
    private final ImportAsyncService importAsyncService;
    private final DatasetProperties datasetProperties;
    private final FeatureDao featureDao;
    private final GdalTool gdalTool;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long importShp(String shpPath) {
        String layerName = FileNameUtils.getFileNameWithoutExtension(shpPath);
        String tableName = datasetTableNameGenerator.getTableName();

        // 创建占位实体，状态为处理中
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(layerName);
        datasetEntity.setDatasetType(DatasetType.SHP.name());
        datasetEntity.setSourceFile(shpPath);
        datasetEntity.setLayerName(layerName);
        datasetEntity.setSchemaName(datasetProperties.getSchema());
        datasetEntity.setTableName(tableName);
        datasetEntity.setStatus(UploadStatus.PROCESSING);
        datasetEntity.setCreatedAt(Instant.now());
        datasetService.save(datasetEntity);

        // 异步执行导入
        importAsyncService.importLayerAsync(datasetEntity, shpPath, layerName, false);

        return datasetEntity.getId();
    }

    @Override
    public Long importShp(String shpPath, Long datasetId) {
        DatasetEntity datasetEntity = checkAndGetDatasetEntity(datasetId);

        importAsyncService.importLayerAsync(datasetEntity, shpPath, null, true);

        return datasetId;
    }

    private DatasetEntity checkAndGetDatasetEntity(Long datasetId) {
        if (!datasetService.updateStatusBySuccess(datasetId, UploadStatus.PROCESSING)) {
            throw new IllegalArgumentException("只能追加导入成功的数据集或数据集不存在");
        }

        return datasetService.getById(datasetId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Long> importGdb(String gdbPath, String layerName) {
        // 同步列出 GDB 图层（较快操作）
        List<String> layerNames = gdalTool.listGdbLayers(gdbPath);
        if (layerNames.isEmpty()) {
            throw new RuntimeException("GDB中未找到任何图层: " + gdbPath);
        }

        // 筛选需要导入的图层
        List<String> targetLayers;
        if (StringUtils.isEmpty(layerName)) {
            targetLayers = layerNames;
        } else {
            if (!layerNames.contains(layerName)) {
                throw new RuntimeException("图层不存在: " + layerName);
            }
            targetLayers = List.of(layerName);
        }

        // 创建占位实体，状态为处理中
        List<DatasetEntity> entities = new ArrayList<>();
        for (String ln : targetLayers) {
            String tableName = datasetTableNameGenerator.getTableName();

            DatasetEntity entity = new DatasetEntity();
            entity.setDatasetName(ln);
            entity.setDatasetType(DatasetType.GDB.name());
            entity.setSourceFile(gdbPath);
            entity.setLayerName(ln);
            entity.setSchemaName(datasetProperties.getSchema());
            entity.setTableName(tableName);
            entity.setStatus(UploadStatus.PROCESSING);
            entity.setCreatedAt(Instant.now());
            entities.add(entity);
        }
        datasetService.saveBatch(entities);

        // 异步执行导入
        for (DatasetEntity entity : entities) {
            importAsyncService.importLayerAsync(entity, gdbPath, entity.getLayerName(), false);
        }

        return entities.stream().map(DatasetEntity::getId).collect(Collectors.toList());
    }

    @Override
    public Long importGdb(String gdbPath, String layerName, Long datasetId) {
        List<String> layerNames = gdalTool.listGdbLayers(gdbPath);
        if (layerNames.isEmpty()) {
            throw new RuntimeException("GDB中未找到任何图层: " + gdbPath);
        }
        if (!layerNames.contains(layerName)) {
            throw new RuntimeException("图层不存在: " + layerName);
        }

        DatasetEntity datasetEntity = checkAndGetDatasetEntity(datasetId);

        importAsyncService.importLayerAsync(datasetEntity, gdbPath, layerName, true);

        return datasetId;
    }

    @Override
    public void uploadGeoJson(UploadGeoJsonDTO dto) {
        FeatureEntity featureEntity = new FeatureEntity();
        featureEntity.setId(IdentifierGeneratorUtils.nextId());
        featureEntity.setName(dto.getName());
        featureDao.saveWithGeoJson(featureEntity, dto.getGeoJson().toString());
    }

    @Override
    public void uploadWkt(UploadWktDTO dto) {
        String wkt = dto.getWkt();
        Integer srid = dto.getSrid();

        FeatureEntity featureEntity = new FeatureEntity();
        featureEntity.setId(IdentifierGeneratorUtils.nextId());
        featureEntity.setName(dto.getName());
        featureEntity.setProperties(JSON.toJSONString(dto.getProperties()));
        featureDao.saveWithWkt(featureEntity, wkt, srid);
    }

    @Override
    public Long importGdbBatch(List<String> paths, String layerName, Integer srid, String tableName) {
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(layerName);
        datasetEntity.setDatasetType(DatasetType.GDB.name());
        datasetEntity.setSourceFile(StringUtils.limit(String.join(",", paths), 10000));
        datasetEntity.setLayerName(layerName);
        datasetEntity.setSchemaName(datasetProperties.getSchema());
        datasetEntity.setTableName(StringUtils.isEmpty(tableName) ? datasetTableNameGenerator.getTableName() : tableName);
        datasetEntity.setStatus(UploadStatus.PROCESSING);
        datasetEntity.setCreatedAt(Instant.now());
        datasetService.save(datasetEntity);

        importAsyncService.importGdbLayersAsync(datasetEntity,
                paths,
                srid,
                StringUtils.isNotBlank(tableName));

        return datasetEntity.getId();
    }

    @Override
    public Long importShpBatch(List<String> paths,
                               String layerName,
                               Integer srid,
                               String tableName) {
        // 创建占位实体，状态为处理中
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(layerName);
        datasetEntity.setDatasetType(DatasetType.SHP.name());
        datasetEntity.setSourceFile(StringUtils.limit(String.join(",", paths), 10000));
        datasetEntity.setLayerName(layerName);
        datasetEntity.setSchemaName(datasetProperties.getSchema());
        datasetEntity.setTableName(StringUtils.isEmpty(tableName) ? datasetTableNameGenerator.getTableName() : tableName);
        datasetEntity.setStatus(UploadStatus.PROCESSING);
        datasetEntity.setCreatedAt(Instant.now());
        datasetService.save(datasetEntity);

        // 异步执行导入
        importAsyncService.importShpLayersAsync(datasetEntity, paths, srid, StringUtils.isNotBlank(tableName));

        return datasetEntity.getId();
    }

}
