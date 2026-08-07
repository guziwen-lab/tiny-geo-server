package com.supermap.support;

import com.supermap.*;
import com.supermap.enums.AnalysisType;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.analyzetask.service.TaskStatusUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAnalysisExecutor {

    private final AnalysisEngine analysisEngine;
    private final TaskStatusUpdateService taskStatusUpdateService;

    @Async("analyzeTaskExecutor")
    public <T extends AnalysisParam> void executeAsync(TaskEntity task, AnalysisType analysisType, AnalysisContext<T> context) {
        try {
            AnalysisResult result = analysisEngine.execute(analysisType, context);

            taskStatusUpdateService.markSuccess(task.getId(), result, context);
        } catch (Exception e) {
            log.error("任务执行失败, taskId={}", task.getId(), e);
            taskStatusUpdateService.markFailed(task.getId(), e.getMessage());
        }
    }

}
