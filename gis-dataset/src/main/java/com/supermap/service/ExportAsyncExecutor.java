package com.supermap.service;

import com.supermap.enums.DatasetType;
import com.supermap.entity.ExportTaskEntity;
import com.supermap.modules.sys.entity.FileEntity;
import com.supermap.modules.sys.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportAsyncExecutor {

    private final ExportStatusUpdater exportStatusUpdater;

    private final FileService fileService;

    @Value("${gdal.pgConn}")
    private String pgConn;

    @Async("exportTaskExecutor")
    public void exportLayerAsync(ExportTaskEntity entity, FileEntity fileEntity) {
        try {
            // 执行 ogr2ogr 导出
            execOgr2ogr(entity.getTableName(), fileEntity.getFilePath(), entity.getExportType());

            // 成功状态回写
            exportStatusUpdater.markSuccess(entity.getId());
        } catch (Exception e) {
            log.error("数据集导出失败, taskId={}, table={}", entity.getId(), entity.getTableName(), e);
            exportStatusUpdater.markFailed(entity.getId(), e.getMessage());
        }
    }

    /**
     * 核心逻辑：执行 ogr2ogr 从 PostgreSQL 导出数据
     */
    private void execOgr2ogr(String tableName, String targetPath, DatasetType exportType) {
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

        cmd.add(targetPath); // 目标输出文件/文件夹路径
        cmd.add(pgConn);     // 源数据库连接串
        cmd.add(tableName);  // 导出表名

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