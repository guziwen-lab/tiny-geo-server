package com.supermap.task.modules.compose.service.impl;

import com.supermap.task.enums.TaskStatus;
import com.supermap.task.modules.analyzetask.entity.TaskEntity;
import com.supermap.task.modules.compose.entity.ComposeEntity;
import com.supermap.task.modules.compose.service.ComposeService;
import com.supermap.task.modules.compose.service.ComposeStatusUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author gzw
 */
@RequiredArgsConstructor
@Service
public class ComposeStatusUpdateServiceImpl implements ComposeStatusUpdateService {

    private final ComposeService composeService;

    @Override
    public void markSuccess(ComposeEntity composeEntity, TaskEntity taskEntity) {
        ComposeEntity update = new ComposeEntity();
        update.setId(composeEntity.getId());
        update.setFinishedAt(Instant.now());
        update.setCost(Instant.now().toEpochMilli() - composeEntity.getStartedAt().toEpochMilli());
        update.setStatus(TaskStatus.SUCCESS);
        update.setResultDatasetId(taskEntity.getResultDatasetId());

        composeService.updateById(update);
    }

    @Override
    public void markFailed(ComposeEntity composeEntity, String message) {
        ComposeEntity update = new ComposeEntity();
        update.setId(composeEntity.getId());
        update.setFinishedAt(Instant.now());
        update.setCost(Instant.now().toEpochMilli() - composeEntity.getStartedAt().toEpochMilli());
        update.setStatus(TaskStatus.FAILED);
        update.setMessage(message);

        composeService.updateById(update);
    }

}
