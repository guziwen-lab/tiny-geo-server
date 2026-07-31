package com.supermap.modules.dataset.service.impl;

import com.supermap.common.util.JSON;
import com.supermap.common.util.StringUtils;
import com.supermap.config.DatasetProperties;
import com.supermap.enums.DatasetType;
import com.supermap.enums.GeomType;
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
import com.supermap.util.ShapeEncodingDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static final Pattern LAYER_PATTERN1 = Pattern.compile("^Layer:\\s+(.+?)\\s*(?:\\(|$)");
    private static final Pattern LAYER_PATTERN2 = Pattern.compile("^\\d+:\\s*(.+?)\\s*\\(");

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long importShp(String shpPath) {
        String layerName = getFileNameWithoutExtension(shpPath);
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
        List<String> layerNames = listGdbLayers(gdbPath);
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
        List<String> layerNames = listGdbLayers(gdbPath);
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

    /**
     * 按实际 SRID（以及不能混存的图层、几何类型）将一批 GDB 归并为数据集。
     * SRID 是坐标处理的最小颗粒度，不同高斯分带不会被写入同一张表。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> importGdbBatchByGrouping(List<BatchImportGdbDTO> dtoList) {
        Map<GdbGroupKey, List<GdbLayerSource>> groups = new LinkedHashMap<>();

        for (BatchImportGdbDTO dto : dtoList) {
            String gdbPath = dto.getPaths();
            if (StringUtils.isEmpty(gdbPath)) {
                throw new IllegalArgumentException("GDB 路径不能为空");
            }
            List<String> layerNames = listGdbLayers(gdbPath);
            if (layerNames.isEmpty()) {
                throw new IllegalArgumentException("GDB中未找到任何图层: " + gdbPath);
            }
            List<String> targetLayers = StringUtils.isEmpty(dto.getLayerName()) ? layerNames : List.of(dto.getLayerName());
            for (String layerName : targetLayers) {
                if (!layerNames.contains(layerName)) {
                    throw new IllegalArgumentException("图层不存在: " + layerName + ", GDB=" + gdbPath);
                }
                LayerMeta meta = importAsyncService.queryLayerMeta(gdbPath, layerName);
                if (meta.srid() == null || meta.srid() <= 0) {
                    throw new IllegalArgumentException("图层没有可识别的 EPSG SRID，无法按坐标系归并: "
                            + gdbPath + " / " + layerName);
                }
                if (GeomType.ofOgr2ogrCode(meta.geomType()) == null) {
                    throw new IllegalArgumentException("图层几何类型不支持: " + meta.geomType());
                }
                GdbGroupKey key = new GdbGroupKey(layerName, meta.srid(), meta.geomType());
                groups.computeIfAbsent(key, ignored -> new ArrayList<>())
                        .add(new GdbLayerSource(gdbPath, layerName));
            }
        }

        List<DatasetEntity> entities = new ArrayList<>();
        List<Map.Entry<GdbGroupKey, List<GdbLayerSource>>> entries = new ArrayList<>(groups.entrySet());
        for (Map.Entry<GdbGroupKey, List<GdbLayerSource>> entry : entries) {
            GdbGroupKey key = entry.getKey();
            DatasetEntity entity = new DatasetEntity();
            entity.setDatasetName(key.layerName() + "_srid_" + key.srid());
            entity.setDatasetType(DatasetType.GDB.name());
            entity.setSourceFile("批量GDB(" + entry.getValue().size() + "个), SRID=" + key.srid());
            entity.setLayerName(key.layerName());
            entity.setSchemaName(datasetProperties.getSchema());
            entity.setTableName(datasetTableNameGenerator.getTableName());
            entity.setStatus(UploadStatus.PROCESSING);
            entity.setCreatedAt(Instant.now());
            entities.add(entity);
        }
        datasetService.saveBatch(entities);
        for (int i = 0; i < entities.size(); i++) {
            importAsyncService.importLayersAsync(entities.get(i),
                    entries.get(i).getValue(),
                    null,
                    false);
        }
        return entities.stream().map(DatasetEntity::getId).toList();
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
        List<GdbLayerSource> sources = new ArrayList<>();
        for (String gdbPath : paths) {
            List<String> layerNames = listGdbLayers(gdbPath);
            if (layerNames.isEmpty())
                throw new IllegalArgumentException("GDB中未找到任何图层: " + gdbPath);
            if (!layerNames.contains(layerName))
                throw new IllegalArgumentException("图层不存在: " + layerName + ", GDB=" + gdbPath);

            GdbLayerSource gdbLayerSource = new GdbLayerSource(gdbPath, layerName);
            sources.add(gdbLayerSource);
        }

        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(layerName);
        datasetEntity.setDatasetType(DatasetType.GDB.name());
//        datasetEntity.setSourceFile(String.join(",", paths));
        datasetEntity.setLayerName(layerName);
        datasetEntity.setSchemaName(datasetProperties.getSchema());
        datasetEntity.setTableName(StringUtils.isEmpty(tableName) ? datasetTableNameGenerator.getTableName() : tableName);
        datasetEntity.setStatus(UploadStatus.PROCESSING);
        datasetEntity.setCreatedAt(Instant.now());
        datasetService.save(datasetEntity);

        importAsyncService.importLayersAsync(datasetEntity,
                sources,
                srid,
                StringUtils.isNotBlank(tableName));

        return datasetEntity.getId();
    }

    @Override
    public Long importShpBatch(List<String> paths,
                               String layerName,
                               Integer srid,
                               String encoding,
                               String tableName) {
        List<GdbLayerSource> sources = new ArrayList<>();
        for (String path : paths) {
            String confirmEncoding = null;
            String ln = getFileNameWithoutExtension(path);

            if (StringUtils.isEmpty(encoding)) {
                confirmEncoding = ShapeEncodingDetector.detect(path, ln);
            }

            GdbLayerSource gdbLayerSource = new GdbLayerSource(path, ln, confirmEncoding);
            sources.add(gdbLayerSource);
        }

        // 创建占位实体，状态为处理中
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(layerName);
        datasetEntity.setDatasetType(DatasetType.SHP.name());
//        datasetEntity.setSourceFile(String.join(",", paths));
        datasetEntity.setLayerName(layerName);
        datasetEntity.setSchemaName(datasetProperties.getSchema());
        datasetEntity.setTableName(StringUtils.isEmpty(tableName) ? datasetTableNameGenerator.getTableName() : tableName);
        datasetEntity.setStatus(UploadStatus.PROCESSING);
        datasetEntity.setCreatedAt(Instant.now());
        datasetService.save(datasetEntity);

        // 异步执行导入
        importAsyncService.importLayersAsync(datasetEntity, sources, srid, StringUtils.isNotBlank(tableName));

        return datasetEntity.getId();
    }

    /**
     * 使用 ogrinfo 列出 GDB 中的所有图层名
     */
    private List<String> listGdbLayers(String gdbPath) {
        try {
            List<String> cmd = new ArrayList<>();
            cmd.add("ogrinfo");
            cmd.add("-so");
            cmd.add(gdbPath);
            log.info("执行查询 GDB 中的所有图层名命令: {}", String.join(" ", cmd));

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            List<String> layers = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = LAYER_PATTERN1.matcher(line);
                    if (matcher.find()) {
                        layers.add(matcher.group(1));
                        continue;
                    }
                    matcher = LAYER_PATTERN2.matcher(line);
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

}
