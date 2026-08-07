package com.supermap.modules.analyzetask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.*;
import com.supermap.config.DatasetProperties;
import com.supermap.config.TaskConfigurationProperties;
import com.supermap.enums.TaskStatus;
import com.supermap.modules.analyzetask.dto.*;
import com.supermap.support.AnalysisContextBuilder;
import com.supermap.support.LayerInfoBuilder;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;

import com.supermap.modules.analyzetask.service.TaskDatasetService;
import com.supermap.support.AsyncAnalysisExecutor;
import com.supermap.task.AnalysisTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.analyzetask.dao.TaskDao;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.analyzetask.service.TaskService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service("taskService")
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskDao, TaskEntity> implements TaskService {

    private final AnalysisEngine analysisEngine;

    private final TaskDatasetService taskDatasetService;

    private final AsyncAnalysisExecutor asyncAnalysisExecutor;

    private final DatasetProperties datasetProperties;

    private final TaskConfigurationProperties taskConfigurationProperties;

    private final AnalysisContextBuilder analysisContextBuilder;

    @Override
    public Page<TaskEntity> queryPage(TaskDTO dto) {
        LambdaQueryWrapper<TaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TaskEntity::getCreatedAt);
        return page(dto.page(), wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TaskEntity create(TaskSaveDTO dto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskName(dto.getTaskName());
        taskEntity.setStatus(TaskStatus.NOT_PROCESSED);
        taskEntity.setAnalysisType(dto.getAnalysisType());
        taskEntity.setTaskParam(dto.getTaskParam());
        taskEntity.setCreatedAt(Instant.now());
        try {
            save(taskEntity);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Task name already exists");
        }

        List<TaskDatasetEntity> taskDatasetEntities = getTaskDatasetEntities(dto.getDatasetIds(), taskEntity);
        taskDatasetService.saveBatch(taskDatasetEntities);

        return taskEntity;
    }

    public static List<TaskDatasetEntity> getTaskDatasetEntities(List<TaskDatasetSaveDTO> datasetIds, TaskEntity taskEntity) {
        List<TaskDatasetEntity> taskDatasetEntities = new ArrayList<>(datasetIds.size());
        for (int i = 0; i < datasetIds.size(); i++) {
            TaskDatasetEntity taskDatasetEntity = new TaskDatasetEntity();
            taskDatasetEntity.setDatasetId(datasetIds.get(i).getDatasetId());
            taskDatasetEntity.setTaskId(taskEntity.getId());
            taskDatasetEntity.setSort(i);
            taskDatasetEntities.add(taskDatasetEntity);
        }
        return taskDatasetEntities;
    }

    @Override
    public void start(Long taskId, StartTaskDTO dto) {
        TaskEntity taskEntity = baseMapper.getStartableById(taskId);
        if (taskEntity == null)
            throw new IllegalArgumentException("Task not found or Task is already processing/success");

        List<DatasetEntity> datasets = getDatasetEntityByTaskId(taskId);

        // 校验数据集是否在config.schema中
        String schemaName = datasetProperties.getSchema();
        for (DatasetEntity dataset : datasets) {
            if (!dataset.getSchemaName().equals(schemaName)) {
                throw new IllegalArgumentException("Datasets must be in the config schema");
            }
        }

        // 标记任务为处理中
        taskEntity.setStatus(TaskStatus.PROCESSING);
        taskEntity.setMessage("");
        taskEntity.setStartedAt(Instant.now());
        updateById(taskEntity);

        AnalysisContext<? extends AnalysisParam> context = buildContext(taskEntity, datasets, dto);

        // 异步执行分析任务
        asyncAnalysisExecutor.executeAsync(taskEntity, taskEntity.getAnalysisType(), context);
    }

    private AnalysisContext<? extends AnalysisParam> buildContext(TaskEntity taskEntity,
                                                        List<DatasetEntity> datasets,
                                                        StartTaskDTO dto) {
        // 构建图层信息
        List<LayerInfo> layerInfos = datasets.stream()
                .map(LayerInfoBuilder::fromDatasetEntity).toList();

        // 构建分析任务参数
        AnalysisTask<?> analysisTask = analysisEngine.getTask(taskEntity.getAnalysisType());

        // 构建分析上下文
        return analysisContextBuilder.buildAnalysisContext(layerInfos,
                analysisTask.buildParam(taskEntity.getTaskParam()),
                StringUtils.isEmpty(dto.getResultTableName()) ?
                        "analyze_" + taskEntity.getId() : dto.getResultTableName());
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