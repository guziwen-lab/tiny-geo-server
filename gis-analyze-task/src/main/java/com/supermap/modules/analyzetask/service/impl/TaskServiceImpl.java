package com.supermap.modules.analyzetask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.*;
import com.supermap.config.DatasetProperties;
import com.supermap.enums.TaskStatus;
import com.supermap.modules.analyzetask.dto.StartTaskDTO;
import com.supermap.modules.analyzetask.dto.TaskDatasetSaveDTO;
import com.supermap.support.LayerInfoBuilder;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;

import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.modules.analyzetask.service.TaskDatasetService;
import com.supermap.modules.analyzetask.service.TaskAsyncService;
import com.supermap.task.AnalysisTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.analyzetask.dao.TaskDao;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.analyzetask.service.TaskService;
import com.supermap.modules.analyzetask.dto.TaskDTO;
import com.supermap.modules.analyzetask.dto.TaskSaveDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service("taskService")
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskDao, TaskEntity> implements TaskService {

    private final AnalysisEngine analysisEngine;

    private final TaskDatasetService taskDatasetService;

    private final TaskAsyncService taskAsyncService;

    private final DatasetService datasetService;

    private final DatasetProperties datasetProperties;

    @Override
    public Page<TaskEntity> queryPage(TaskDTO dto) {
        LambdaQueryWrapper<TaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TaskEntity::getCreatedAt);
        return page(dto.page(), wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(TaskSaveDTO dto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskName(dto.getTaskName());
        taskEntity.setStatus(TaskStatus.NOT_PROCESSED);
        taskEntity.setAnalysisType(dto.getAnalysisType());
        taskEntity.setTaskParam(dto.getTaskParam());
        taskEntity.setCreatedAt(Instant.now());
        save(taskEntity);

        List<TaskDatasetSaveDTO> datasetIds = dto.getDatasetIds();
        List<TaskDatasetEntity> taskDatasetEntities = new ArrayList<>(datasetIds.size());
        for (int i = 0; i < datasetIds.size(); i++) {
            TaskDatasetEntity taskDatasetEntity = new TaskDatasetEntity();
            taskDatasetEntity.setDatasetId(datasetIds.get(i).getDatasetId());
            taskDatasetEntity.setTaskId(taskEntity.getId());
            taskDatasetEntity.setSort(i);
            taskDatasetEntities.add(taskDatasetEntity);
        }
        taskDatasetService.saveBatch(taskDatasetEntities);

        return taskEntity.getId();
    }

    @Override
    public void start(Long taskId, StartTaskDTO dto) {
        TaskEntity taskEntity = getById(taskId);
        if (taskEntity == null)
            throw new IllegalArgumentException("Task not found");
        if (taskEntity.getStatus().equals(TaskStatus.PROCESSING))
            throw new IllegalArgumentException("Task is already processing");
        if (taskEntity.getStatus().equals(TaskStatus.SUCCESS))
            throw new IllegalArgumentException("Task is already completed");

        List<DatasetEntity> datasets = getDatasetEntityByTaskId(taskId);

        // 校验数据集是否在config.schema中
        String schemaName = datasetProperties.getSchema();
        for (DatasetEntity dataset : datasets) {
            if (!dataset.getSchemaName().equals(schemaName)) {
                throw new IllegalArgumentException("Datasets must be in the config schema");
            }
        }

        // 构建图层信息
        List<LayerInfo> layerInfos = buildLayerInfo(datasets);

        // 标记任务为处理中
        taskEntity.setStatus(TaskStatus.PROCESSING);
        taskEntity.setSchemaName(schemaName);

        taskEntity.setMessage("");
        updateById(taskEntity);

        // 构建分析上下文
        AnalysisContext<AnalysisParam> context = new AnalysisContext<>();
        context.setTaskId(taskId);
        context.setInputLayers(layerInfos);
        context.setResultLayerName(StringUtils.isEmpty(dto.getResultLayerName()) ?
                taskEntity.getTaskName() : dto.getResultLayerName());
        context.setSchema(datasetProperties.getSchema());
        context.setResultTableName("analyze_" + taskId);

        AnalysisTask<?> analysisTask = analysisEngine.getTask(taskEntity.getAnalysisType());
        context.setParam(analysisTask.buildParam(taskEntity.getTaskParam()));

        // 异步执行分析任务
        taskAsyncService.executeAsync(taskEntity, taskEntity.getAnalysisType(), context);
    }

    /**
     * 构建数据集信息
     *
     * @param datasets 数据集列表
     * @return 图层信息列表
     */
    private List<LayerInfo> buildLayerInfo(List<DatasetEntity> datasets) {
        return datasets.stream().map(LayerInfoBuilder::fromDatasetEntity).toList();
    }

    /**
     * 根据任务id获取数据集
     *
     * @param taskId 任务id
     * @return 数据集列表
     */
    private List<DatasetEntity> getDatasetEntityByTaskId(Long taskId) {
        return taskDatasetService.getDatasetEntityByTaskId(taskId);
    }

}