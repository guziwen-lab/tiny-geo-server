package com.supermap.modules.analyze.executor;

import com.supermap.AnalysisResult;
import com.supermap.enumeration.TaskStatus;
import com.supermap.modules.analyze.dao.TaskDao;
import com.supermap.modules.analyze.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TaskStatusUpdater {

    private final TaskDao taskDao;

    @Transactional(rollbackFor = Exception.class)
    public void markSuccess(Long taskId, AnalysisResult result) {
        TaskEntity task = taskDao.selectById(taskId);
        task.setStatus(TaskStatus.SUCCESS);
        task.setResultTableName(result.getResultTableName());
        task.setFeatureCount(result.getFeatureCount());
        task.setCost(result.getCost());
        task.setFinishedAt(Instant.now());
        taskDao.updateById(task);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markFailed(Long taskId, String message) {
        TaskEntity task = taskDao.selectById(taskId);
        task.setStatus(TaskStatus.FAILED);
        task.setFinishedAt(Instant.now());
        task.setMessage(message);
        taskDao.updateById(task);
    }

}
