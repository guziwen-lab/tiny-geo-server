package com.supermap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import com.supermap.common.util.FileUtils;
import com.supermap.enums.DatasetType;
import com.supermap.enums.UploadStatus;
import com.supermap.service.ExportAsyncExecutor;
import com.supermap.modules.sys.entity.FileEntity;
import com.supermap.modules.sys.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.dao.ExportTaskDao;
import com.supermap.entity.ExportTaskEntity;
import com.supermap.service.ExportTaskService;
import com.supermap.dto.ExportTaskDTO;
import com.supermap.dto.ExportTaskSaveDTO;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@RequiredArgsConstructor
@Service("exportTaskService")
public class ExportTaskServiceImpl extends ServiceImpl<ExportTaskDao, ExportTaskEntity> implements ExportTaskService {

    private final FileService fileService;

    private final ExportAsyncExecutor exportAsyncExecutor;

    @Override
    public Page<ExportTaskEntity> queryPage(ExportTaskDTO dto) {
        LambdaQueryWrapper<ExportTaskEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(ExportTaskSaveDTO dto) {
        ExportTaskEntity exportTaskEntity = new ExportTaskEntity();
        BeanUtils.copyProperties(dto, exportTaskEntity);
        save(exportTaskEntity);
        return exportTaskEntity.getId();
    }

    @Override
    public void updateDTOById(ExportTaskSaveDTO dto) {
        ExportTaskEntity exportTaskEntity = new ExportTaskEntity();
        BeanUtils.copyProperties(dto, exportTaskEntity);
        updateById(exportTaskEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long exportShp(String tableName) {
        return createAndTriggerTask(tableName, DatasetType.SHP);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long exportGdb(String tableName) {
        return createAndTriggerTask(tableName, DatasetType.GDB);
    }

    private Long createAndTriggerTask(String tableName, DatasetType exportType) {
        String destDir = fileService.getFilePath(exportType.getExtension());
        FileUtils.mkdir(destDir);
        String fileName = tableName + "." + exportType.getExtension();

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(fileName);
        fileEntity.setFilePath(destDir);
        fileEntity.setFileType(exportType.getExtension());
        fileEntity.setStorageType("local");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        fileEntity.setCreateTime(now);
        fileEntity.setUpdateTime(now);
        fileEntity.setRefCount(0);
        fileService.save(fileEntity);

        ExportTaskEntity entity = new ExportTaskEntity();
        entity.setTableName(tableName);
        entity.setExportType(exportType);
        entity.setFileId(fileEntity.getId());
        entity.setStatus(UploadStatus.PROCESSING);
        entity.setCreatedAt(Instant.now());
        save(entity);

        // 异步执行导出，传入配置的格式类型
        exportAsyncExecutor.exportLayerAsync(entity, fileEntity);

        return entity.getId();
    }

}