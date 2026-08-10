package com.supermap.support.compose;

import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.supermap.modules.compose.entity.ComposeEntity;
import com.supermap.modules.compose.service.ComposeStatusUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncComposeExecutor {

    private final ComposeStatusUpdateService composeStatusUpdateService;

    @Async("composeExecutor")
    public void executeAsync(ComposeEntity composeEntity, ComposeCallback composeCallback) {
        try {
            TaskEntity taskEntity = composeCallback.accept(composeEntity);
            composeStatusUpdateService.markSuccess(composeEntity, taskEntity);
        } catch (Exception e) {
            log.error("组合任务执行失败, composeId={}", composeEntity.getId(), e);
            composeStatusUpdateService.markFailed(composeEntity, e.getMessage());
        }
    }

}
