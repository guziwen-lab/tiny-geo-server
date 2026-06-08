package com.supermap.modules.analyze.executor;

import com.supermap.AnalysisResult;
import com.supermap.enumeration.TaskStatus;
import com.supermap.modules.analyze.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TaskStatusUpdater {

    private final TaskService taskService;

    @Transactional(rollbackFor = Exception.class)
    public void markSuccess(Long taskId, AnalysisResult result) {
        var task = taskService.getById(taskId);
        task.setStatus(TaskStatus.SUCCESS);
        task.setResultTableName(result.getResultTableName());
        task.setFeatureCount(result.getFeatureCount());
        task.setCost(result.getCost());
        task.setFinishedAt(Instant.now());
        taskService.updateById(task);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markFailed(Long taskId, String message) {
        var task = taskService.getById(taskId);
        task.setStatus(TaskStatus.FAILED);
        task.setFinishedAt(Instant.now());
        task.setMessage(message);
        taskService.updateById(task);
    }

}
