package com.supermap.modules.compose.service;

import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.compose.entity.ComposeEntity;

/**
 * @author gzw
 */
public interface ComposeStatusUpdateService {

    void markSuccess(ComposeEntity composeEntity, TaskEntity taskEntity);

    void markFailed(ComposeEntity composeEntity, String message);

}
