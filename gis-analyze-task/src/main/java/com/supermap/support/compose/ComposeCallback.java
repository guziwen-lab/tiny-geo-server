package com.supermap.support.compose;

import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.compose.entity.ComposeEntity;

/**
 * @author gzw
 */
@FunctionalInterface
public interface ComposeCallback {

    TaskEntity accept(ComposeEntity composeEntity);

}
