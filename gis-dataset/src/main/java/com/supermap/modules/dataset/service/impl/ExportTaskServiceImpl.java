package com.supermap.modules.dataset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import com.supermap.common.util.CollectionUtils;
import com.supermap.common.util.FileUtils;
import com.supermap.enums.DatasetType;
import com.supermap.enums.UploadStatus;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.modules.dataset.service.ExportAsyncExecutor;
import com.supermap.modules.sys.entity.FileEntity;
import com.supermap.modules.sys.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.dataset.dao.ExportTaskDao;
import com.supermap.modules.dataset.entity.ExportTaskEntity;
import com.supermap.modules.dataset.service.ExportTaskService;
import com.supermap.modules.dataset.dto.ExportTaskDTO;
import com.supermap.modules.dataset.dto.ExportTaskSaveDTO;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("exportTaskService")
public class ExportTaskServiceImpl extends ServiceImpl<ExportTaskDao, ExportTaskEntity> implements ExportTaskService {

    private final FileService fileService;

    private final ExportAsyncExecutor exportAsyncExecutor;

    private final DatasetService datasetService;

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
    public Long exportShp(Long datasetId) {
        DatasetEntity datasetEntity = datasetService.getById(datasetId);
        if (datasetEntity == null)
            throw new RuntimeException("数据集不存在");

        return createAndTriggerTask(Collections.singletonList(datasetEntity.getTableName()), DatasetType.SHP);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long exportGdb(List<Long> datasetIds) {
        List<DatasetEntity> datasetEntities = datasetService.listByIds(datasetIds);
        if (CollectionUtils.isEmpty(datasetEntities))
            throw new RuntimeException("数据集不存在");

        List<String> tableNames = datasetEntities.stream()
                .map(DatasetEntity::getTableName)
                .collect(Collectors.toList());

        return createAndTriggerTask(tableNames, DatasetType.GDB);
    }

    private Long createAndTriggerTask(List<String> tableNames, DatasetType exportType) {
        String firstTable = tableNames.get(0);
        String destDir = fileService.getFilePath(exportType.getExtension());
        FileUtils.mkdir(destDir);
        String fileName = firstTable + "." + exportType.getExtension();

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

        ExportTaskEntity taskEntity = new ExportTaskEntity();
        taskEntity.setTableName(String.join(",", tableNames));
        taskEntity.setExportType(exportType);
        taskEntity.setFileId(fileEntity.getId());
        taskEntity.setStatus(UploadStatus.PROCESSING);
        taskEntity.setCreatedAt(Instant.now());
        save(taskEntity);

        // 异步执行导出
        exportAsyncExecutor.exportLayerAsync(taskEntity, fileEntity);

        return taskEntity.getId();
    }

}