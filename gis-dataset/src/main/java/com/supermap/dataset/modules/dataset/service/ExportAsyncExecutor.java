package com.supermap.dataset.modules.dataset.service;

import com.supermap.gdal.GdalTool;
import com.supermap.gis.enums.GeomType;
import com.supermap.dataset.modules.dataset.entity.ExportTaskEntity;
import com.supermap.modules.sys.entity.FileEntity;
import com.supermap.gis.service.GeometryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportAsyncExecutor {

    private final ExportStatusUpdater exportStatusUpdater;
    private final GeometryService geometryService;
    private final GdalTool gdalTool;

    @Async("exportTaskExecutor")
    public void exportLayerAsync(ExportTaskEntity taskEntity, FileEntity fileEntity) {
        try {
            // tableName 字段可能包含逗号分隔的多个表名
            List<String> tableNames = Arrays.asList(taskEntity.getTableName().split(","));

            for (int i = 0; i < tableNames.size(); i++) {
                String tableName = tableNames.get(i);
                boolean append = (i > 0);

                // 查询实际几何类型，解决泛型 geometry 列导致 OpenFileGDB 报 "Unsupported geometry type" 的问题
                GeomType geomType = geometryService.resolveActualGeomType(tableName, "GEOMETRY");
                switch (taskEntity.getExportType()) {
                    case SHP:
                        gdalTool.exportShp(tableName, fileEntity.getFilePath(), geomType, append);
                        break;
                    case GDB:
                        gdalTool.exportGdb(tableName, fileEntity.getFilePath(), geomType, append);
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的导出类型: " + taskEntity.getExportType());
                }
            }

            exportStatusUpdater.markSuccess(taskEntity.getId());
        } catch (Exception e) {
            log.error("数据集导出失败, taskId={}, table={}", taskEntity.getId(), taskEntity.getTableName(), e);
            exportStatusUpdater.markFailed(taskEntity.getId(), e.getMessage());
        }
    }

}