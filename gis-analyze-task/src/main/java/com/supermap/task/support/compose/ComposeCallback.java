package com.supermap.task.support.compose;

import com.supermap.task.modules.task.entity.TaskEntity;

/**
 * @author gzw
 */
@FunctionalInterface
public interface ComposeCallback {

    TaskEntity accept();

}
