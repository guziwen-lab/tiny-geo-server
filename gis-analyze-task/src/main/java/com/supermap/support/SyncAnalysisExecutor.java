package com.supermap.support;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisEngine;
import com.supermap.AnalysisParam;
import com.supermap.AnalysisResult;
import com.supermap.enums.AnalysisType;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.analyzetask.service.TaskStatusUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncAnalysisExecutor {

    private final AnalysisEngine analysisEngine;
    private final TaskStatusUpdateService taskStatusUpdateService;

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
