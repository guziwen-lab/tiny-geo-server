package com.supermap.service.impl;

import com.supermap.common.util.StringUtils;
import com.supermap.enums.DatasetType;
import com.supermap.enums.UploadStatus;
import com.supermap.entity.DatasetEntity;
import com.supermap.service.ImportAsyncExecutor;
import com.supermap.service.DatasetService;
import com.supermap.service.ImportService;
import com.supermap.util.DatasetTableNameGenerator;
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
    private final ImportAsyncExecutor importAsyncExecutor;

    private static final String LAYER_PATTERN = "^Layer:\\s+(.+?)\\s*(?:\\(|$)";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long importShp(String shpPath) {
        String layerName = getFileNameWithoutExtension(shpPath);
        String tableName = datasetTableNameGenerator.getTableName();

        // 创建占位实体，状态为处理中
        DatasetEntity entity = new DatasetEntity();
        entity.setDatasetName(layerName);
        entity.setDatasetType(DatasetType.SHP.name());
        entity.setSourceFile(shpPath);
        entity.setLayerName(layerName);
        entity.setTableName(tableName);
        entity.setStatus(UploadStatus.PROCESSING);
        entity.setCreatedAt(Instant.now());
        datasetService.save(entity);

        // 异步执行导入
        importAsyncExecutor.importLayerAsync(entity, shpPath, null);

        return entity.getId();
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
            entity.setTableName(tableName);
            entity.setStatus(UploadStatus.PROCESSING);
            entity.setCreatedAt(Instant.now());
            entities.add(entity);
        }
        datasetService.saveBatch(entities);

        // 异步执行导入
        for (DatasetEntity entity : entities) {
            importAsyncExecutor.importLayerAsync(entity, gdbPath, entity.getLayerName());
        }

        return entities.stream().map(DatasetEntity::getId).collect(Collectors.toList());
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
