package com.supermap.modules.analyzetask.service;

import com.supermap.AnalysisResult;
import com.supermap.enums.TaskStatus;
import com.supermap.modules.analyzetask.dao.TaskDao;
import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.dataset.entity.DatasetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TaskStatusUpdateService {

    private final TaskDao taskDao;

    @Transactional(rollbackFor = Exception.class)
    public void markSuccess(Long taskId, AnalysisResult result, DatasetEntity resultDatasetEntity) {
        TaskEntity task = taskDao.selectById(taskId);
        task.setStatus(TaskStatus.SUCCESS);
        task.setResultTableName(result.getResultTableName());
        task.setFeatureCount(result.getFeatureCount());
        task.setCost(result.getCost());
        task.setFinishedAt(Instant.now());
        task.setResultDatasetId(resultDatasetEntity.getId());
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
