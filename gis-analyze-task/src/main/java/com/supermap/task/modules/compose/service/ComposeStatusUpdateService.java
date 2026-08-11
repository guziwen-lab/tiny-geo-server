package com.supermap.task.modules.compose.service;

import com.supermap.task.modules.task.entity.TaskEntity;
import com.supermap.task.modules.compose.entity.ComposeEntity;

/**
 * @author gzw
 */
public interface ComposeStatusUpdateService {

    void markSuccess(ComposeEntity composeEntity, TaskEntity taskEntity);

    void markFailed(ComposeEntity composeEntity, String message);

}
