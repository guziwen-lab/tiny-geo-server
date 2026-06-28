package com.supermap.modules.analyze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.*;
import com.supermap.common.util.BeanUtils;
import com.supermap.enums.GeomType;
import com.supermap.enums.TaskStatus;
import com.supermap.modules.analyze.dto.StartTaskDTO;
import com.supermap.modules.analyze.dto.TaskDatasetSaveDTO;
import com.supermap.modules.analyze.entity.DatasetEntity;
import com.supermap.modules.analyze.entity.TaskDatasetEntity;

import com.supermap.modules.analyze.service.DatasetService;
import com.supermap.modules.analyze.service.TaskDatasetService;
import com.supermap.modules.analyze.executor.TaskAsyncExecutor;
import com.supermap.resolver.GeomTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.analyze.dao.TaskDao;
import com.supermap.modules.analyze.entity.TaskEntity;
import com.supermap.modules.analyze.service.TaskService;
import com.supermap.modules.analyze.dto.TaskDTO;
import com.supermap.modules.analyze.dto.TaskSaveDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service("taskService")
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskDao, TaskEntity> implements TaskService {

    private final AnalysisEngine analysisEngine;

    private final TaskDatasetService taskDatasetService;

    private final TaskAsyncExecutor taskAsyncExecutor;

    private final DatasetService datasetService;

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
        taskEntity.setSubType(dto.getSubType());
        taskEntity.setCreatedAt(Instant.now());

        GeomType geomType = dto.getGeomType();
        List<TaskDatasetSaveDTO> datasetIds = dto.getDatasetIds();
        if (geomType == null) {
            // 判断导出的类型
            List<DatasetEntity> datasetEntities = datasetService.listByIds(datasetIds.stream()
                    .map(TaskDatasetSaveDTO::getDatasetId).toList());
            geomType = GeomTypeResolver.resolve(dto.getAnalysisType(), datasetEntities);
        }
        taskEntity.setGeomType(geomType);
        save(taskEntity);

        List<TaskDatasetEntity> taskDatasetEntities = datasetIds.stream()
                .map(item -> {
                    TaskDatasetEntity taskDatasetEntity = new TaskDatasetEntity();
                    taskDatasetEntity.setDatasetId(item.getDatasetId());
                    taskDatasetEntity.setTaskId(taskEntity.getId());
                    return taskDatasetEntity;
                }).toList();
        taskDatasetService.saveBatch(taskDatasetEntities);

        return taskEntity.getId();
    }

    @Override
    public void updateDTOById(TaskSaveDTO dto) {
        TaskEntity taskEntity = new TaskEntity();
        BeanUtils.copyProperties(dto, taskEntity);
        updateById(taskEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void start(Long taskId, StartTaskDTO dto) {
        TaskEntity task = getById(taskId);
        if (task == null)
            throw new IllegalArgumentException("Task not found");
        if (task.getStatus().equals(TaskStatus.PROCESSING))
            throw new IllegalArgumentException("Task is already processing");
        if (task.getStatus().equals(TaskStatus.SUCCESS))
            throw new IllegalArgumentException("Task is already completed");

        List<TaskDatasetEntity> taskDatasetEntities = taskDatasetService.getByTaskId(taskId);

        List<DatasetEntity> datasets = datasetService
                .listByIds(taskDatasetEntities.stream().map(TaskDatasetEntity::getDatasetId).toList());

        if (datasets.size() < 2) {
            throw new IllegalArgumentException("Overlay analysis requires at least 2 datasets");
        }

        // 标记任务为处理中
        task.setStatus(TaskStatus.PROCESSING);
        updateById(task);

        // 构建分析上下文
        AnalysisContext<AnalysisParam> context = new AnalysisContext<>();
        context.setTaskId(taskId);
        context.setInputLayers(datasets.stream().map(item -> {
            LayerInfo layerInfo = new LayerInfo();
            layerInfo.setTableName(item.getTableName());
            layerInfo.setGeomType(item.getGeomType());
            layerInfo.setSrid(item.getSrid());
            return layerInfo;
        }).toList());
        context.setResultLayerName(task.getTaskName());
        context.setResultTableName("analyze_" + taskId);

        AnalysisTask<?> analysisTask = analysisEngine.getTask(task.getAnalysisType());
        context.setParam(analysisTask.buildParam(task.getSubType()));

        // 异步执行分析任务
        taskAsyncExecutor.executeAsync(task, task.getAnalysisType(), context);
    }

}