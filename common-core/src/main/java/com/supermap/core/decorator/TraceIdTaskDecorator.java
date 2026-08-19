package com.supermap.core.decorator;

import com.supermap.core.constant.TraceIdConstant;
import com.supermap.core.util.UUIDUtils;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

public class TraceIdTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 优先继承提交线程（父线程）的 traceId，HTTP 请求触发的 @Async 可沿用同一链路；
        // 定时任务等无父线程 traceId 的场景则生成新的
        String parentTraceId = MDC.get(TraceIdConstant.TRACE_ID);
        String traceId = (parentTraceId != null && !parentTraceId.isEmpty())
                ? parentTraceId
                : UUIDUtils.get();
        return () -> {
            MDC.put(TraceIdConstant.TRACE_ID, traceId);
            try {
                runnable.run();
            } finally {
                MDC.remove(TraceIdConstant.TRACE_ID);
            }
        };
    }

}