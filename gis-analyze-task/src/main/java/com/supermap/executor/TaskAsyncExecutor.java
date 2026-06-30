package com.supermap.executor;

import com.supermap.*;
import com.supermap.common.util.CollectionUtils;
import com.supermap.enums.AnalysisType;
import com.supermap.entity.DatasetEntity;
import com.supermap.entity.TaskEntity;
import com.supermap.entity.TaskStepEntity;
import com.supermap.service.TaskStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAsyncExecutor {

    private final AnalysisEngine analysisEngine;
    private final TaskStepService taskStepService;
    private final TaskStatusUpdater taskStatusUpdater;

    @Async("analyzeTaskExecutor")
    public void executeAsync(TaskEntity task, AnalysisType analysisType, AnalysisContext<AnalysisParam> context) {
        try {
            AnalysisResult result = analysisEngine.execute(analysisType, context);

            saveSteps(task, context.getSteps());
            taskStatusUpdater.markSuccess(task.getId(), result);
        } catch (Exception e) {
            log.error("任务执行失败, taskId={}", task.getId(), e);
            taskStatusUpdater.markFailed(task.getId(), e.getMessage());
        }
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

            // 把步骤记录到数据集表
            DatasetEntity datasetEntity = new DatasetEntity();
            datasetEntity.setDatasetName(step.getOutputTable());
            datasetEntity.setLayerName(step.getOutputTable());
            datasetEntity.setTableName(step.getOutputTable());
            datasetEntity.setGeomType(task.getGeomType());
//            datasetEntity.setSrid(task.getSrid());
        }

        taskStepService.saveBatch(stepEntities);

    }

}
