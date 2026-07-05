package com.supermap.modules.analyzetask.service;

import com.supermap.*;
import com.supermap.enums.AnalysisType;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAsyncService {

    private final AnalysisEngine analysisEngine;
    private final TaskStatusUpdateService taskStatusUpdateService;

    @Async("analyzeTaskExecutor")
    public void executeAsync(TaskEntity task, AnalysisType analysisType, AnalysisContext<AnalysisParam> context) {
        try {
            AnalysisResult result = analysisEngine.execute(analysisType, context);

            taskStatusUpdateService.markSuccess(task, result, context);
        } catch (Exception e) {
            log.error("任务执行失败, taskId={}", task.getId(), e);
            taskStatusUpdateService.markFailed(task.getId(), e.getMessage());
        }
    }

}
