package com.supermap.task.support.analysis;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisEngine;
import com.supermap.analyze.AnalysisParam;
import com.supermap.analyze.AnalysisResult;
import com.supermap.analyze.enums.AnalysisType;
import com.supermap.task.modules.task.entity.TaskEntity;
import com.supermap.task.modules.task.service.TaskStatusUpdateService;
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

    public <T extends AnalysisParam> void execute(TaskEntity task, AnalysisType analysisType, AnalysisContext<T> context) {
        try {
            AnalysisResult result = analysisEngine.execute(analysisType, context);

            taskStatusUpdateService.markSuccess(task.getId(), result, context);
        } catch (Exception e) {
            taskStatusUpdateService.markFailed(task.getId(), e.getMessage());
            throw new RuntimeException("任务执行失败, taskId=" + task.getId(), e);
        }
    }

}
