package com.supermap.modules.dataset.service;

import com.supermap.config.DatasetProperties;
import com.supermap.enums.DatasetType;
import com.supermap.enums.GeomType;
import com.supermap.modules.dataset.entity.ExportTaskEntity;
import com.supermap.modules.sys.entity.FileEntity;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportAsyncExecutor {

    private final ExportStatusUpdater exportStatusUpdater;

    private final DatasetProperties datasetProperties;

    private final GeometryService geometryService;

    @Async("exportTaskExecutor")
    public void exportLayerAsync(ExportTaskEntity taskEntity, FileEntity fileEntity) {
        try {
            // tableName 字段可能包含逗号分隔的多个表名
            List<String> tableNames = Arrays.asList(taskEntity.getTableName().split(","));

            for (int i = 0; i < tableNames.size(); i++) {
                String tableName = tableNames.get(i);
                boolean append = (i > 0);
                execOgr2ogr(tableName, fileEntity.getFilePath(), taskEntity.getExportType(), append);
            }

            exportStatusUpdater.markSuccess(taskEntity.getId());
        } catch (Exception e) {
            log.error("数据集导出失败, taskId={}, table={}", taskEntity.getId(), taskEntity.getTableName(), e);
            exportStatusUpdater.markFailed(taskEntity.getId(), e.getMessage());
        }
    }

    /**
     * 核心逻辑：执行 ogr2ogr 从 PostgreSQL 导出数据
     *
     * @param append 是否为追加模式（多表导出到同一个 GDB 时，第二张表起使用追加模式）
     */
    private void execOgr2ogr(String tableName, String targetPath, DatasetType exportType, boolean append) {
        String schema = datasetProperties.getSchema();
        String qualifiedTableName = schema + "." + tableName;

        // 查询实际几何类型，解决泛型 geometry 列导致 OpenFileGDB 报 "Unsupported geometry type" 的问题
        GeomType geomType = geometryService.resolveActualGeomType(qualifiedTableName, "GEOMETRY");

        List<String> cmd = new ArrayList<>();
        cmd.add("ogr2ogr");
        cmd.add("-f");

        // 根据传入类型适配 GDAL 驱动名
        if (DatasetType.SHP == exportType) {
            cmd.add("ESRI Shapefile");
            // 附带编码配置防中文乱码
            cmd.add("--config");
            cmd.add("SHAPE_ENCODING");
            cmd.add("GBK");
        } else if (DatasetType.GDB == exportType) {
            cmd.add("OpenFileGDB");
        } else {
            throw new IllegalArgumentException("不支持的导出类型: " + exportType);
        }

        // 追加模式：多表导出到同一个目标文件时，第二张表起需要 -update -append
        if (append) {
            cmd.add("-update");
            cmd.add("-append");
        }

        // 明确指定输出几何类型，避免泛型 geometry 列导致导出失败
        cmd.add("-nlt");
        cmd.add(geomType.getOgr2ogrNltValue());

        cmd.add(targetPath); // 目标输出文件/文件夹路径
        cmd.add(datasetProperties.getPgConnect());     // 源数据库连接串
        cmd.add(qualifiedTableName);  // schema.tableName

        log.info("执行导出命令: {}", String.join(" ", cmd));

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
                log.error("ogr2ogr 导出失败, exitCode={}, output={}", code, output);
                throw new RuntimeException("ogr2ogr 导出失败(exitCode=" + code + "): " + output);
            }
            log.info("导出成功, table={}, output={}", tableName, output);
        } catch (IOException e) {
            throw new RuntimeException("执行 ogr2ogr 失败，请确认已安装并配置 GDAL 环境", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ogr2ogr 导出过程被中断", e);
        }
    }

}