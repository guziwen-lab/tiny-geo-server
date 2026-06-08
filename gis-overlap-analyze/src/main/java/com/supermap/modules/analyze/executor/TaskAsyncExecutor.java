package com.supermap.modules.analyze.executor;

import com.supermap.*;
import com.supermap.common.util.CollectionUtils;
import com.supermap.enumeration.AnalysisType;
import com.supermap.modules.analyze.entity.TaskStepEntity;
import com.supermap.modules.analyze.service.TaskStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAsyncExecutor {

    private final AnalysisEngine analysisEngine;
    private final TaskStepService taskStepService;
    private final TaskStatusUpdater taskStatusUpdater;

    @Async("analyzeTaskExecutor")
    public void executeAsync(Long taskId, AnalysisType analysisType, AnalysisContext<AnalysisParam> context) {
        try {
            AnalysisResult result = analysisEngine.execute(analysisType, context);

            saveSteps(taskId, context.getSteps());
            taskStatusUpdater.markSuccess(taskId, result);
        } catch (Exception e) {
            log.error("任务执行失败, taskId={}", taskId, e);
            taskStatusUpdater.markFailed(taskId, e.getMessage());
        }
    }

    private void saveSteps(Long taskId, List<AnalysisStep> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            return;
        }
        List<TaskStepEntity> entities = steps.stream().map(step -> {
            TaskStepEntity entity = new TaskStepEntity();
            entity.setTaskId(taskId);
            entity.setStepNo(step.getStepNo());
            entity.setInputTable(step.getInputTable());
            entity.setOverlayTable(step.getOverlayTable());
            entity.setOutputTable(step.getOutputTable());
            return entity;
        }).toList();
        taskStepService.saveBatch(entities);
    }

}
