package com.supermap.support.compose;

import com.supermap.modules.analyzetask.entity.TaskEntity;

/**
 * @author gzw
 */
@FunctionalInterface
public interface ComposeCallback {

    TaskEntity accept();

}
