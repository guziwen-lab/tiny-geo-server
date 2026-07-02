package com.supermap.modules.analyzetask.executor;

import com.supermap.*;
import com.supermap.common.util.CollectionUtils;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.UploadStatus;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.analyzetask.entity.TaskStepEntity;
import com.supermap.modules.dataset.service.DatasetService;
import com.supermap.modules.analyzetask.service.TaskStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAsyncExecutor {

    private final AnalysisEngine analysisEngine;
    private final TaskStepService taskStepService;
    private final TaskStatusUpdater taskStatusUpdater;
    private final DatasetService datasetService;

    @Async("analyzeTaskExecutor")
    public void executeAsync(TaskEntity task, AnalysisType analysisType, AnalysisContext<AnalysisParam> context) {
        try {
            AnalysisResult result = analysisEngine.execute(analysisType, context);

            saveSteps(task, context.getSteps());
            taskStatusUpdater.markSuccess(task.getId(), result);

            // 结果记录到数据集
            saveResultToDataset(result, context);
        } catch (Exception e) {
            log.error("任务执行失败, taskId={}", task.getId(), e);
            taskStatusUpdater.markFailed(task.getId(), e.getMessage());
        }
    }

    private void saveResultToDataset(AnalysisResult result, AnalysisContext<AnalysisParam> context) {
        DatasetEntity datasetEntity = new DatasetEntity();
        datasetEntity.setDatasetName(result.getResultTableName());
        datasetEntity.setLayerName(result.getResultLayerName());
        datasetEntity.setSchemaName(result.getSchemaName());
        datasetEntity.setTableName(result.getResultTableName());
        datasetEntity.setGeomType(result.getGeomType());
        datasetEntity.setSrid(result.getSrid());
        datasetEntity.setFeatureCount(result.getFeatureCount());
        datasetEntity.setCreatedAt(Instant.now());
        datasetEntity.setStatus(UploadStatus.SUCCESS);
        datasetEntity.setSchemaName(context.getSchema());
        datasetService.save(datasetEntity);
    }

    private void saveSteps(TaskEntity task, List<AnalysisStep> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            return;
        }

        List<TaskStepEntity> stepEntities = new ArrayList<>(steps.size());
        for (AnalysisStep step : steps) {
            // 保存步骤
            TaskStepEntity stepEntity = new TaskStepEntity();
            stepEntity.setTaskId(task.getId());
            stepEntity.setStepNo(step.getStepNo());
            stepEntity.setInputTable(step.getInputTable());
            stepEntity.setOverlayTable(step.getOverlayTable());
            stepEntity.setOutputTable(step.getOutputTable());
            stepEntities.add(stepEntity);
        }

        taskStepService.saveBatch(stepEntities);

    }

}
