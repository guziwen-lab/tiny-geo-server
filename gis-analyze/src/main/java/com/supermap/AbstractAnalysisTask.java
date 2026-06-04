package com.supermap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAnalysisTask<T extends AnalysisParam> implements AnalysisTask<T> {

    @Override
    public AnalysisResult execute(AnalysisContext<T> context) {
        long start = System.currentTimeMillis();

        try {
            validate(context);

            beforeExecute(context);

            AnalysisResult result = doExecute(context);

            long cost = System.currentTimeMillis() - start;
            result.setCost(cost);

            afterExecute(context, result);

            return result;
        } catch (Exception e) {
            onError(context, e);
            throw e;
        } finally {
            logCost(start);
            cleanUp(context);
        }
    }

    /**
     * 参数校验
     * <p>
     * Overlay检查图层是否存在
     * Buffer检查距离参数是否合法
     * Clip检查裁剪图层是否存在
     */
    protected void validate(AnalysisContext<T> context) {
    }

    /**
     * 执行前处理
     */
    protected void beforeExecute(AnalysisContext<T> context) {
        log.info("[{}] analysis start, context={}", getTaskName(), context);
    }

    /**
     * 核心分析逻辑
     */
    protected abstract AnalysisResult doExecute(AnalysisContext<T> context);

    /**
     * 执行成功后处理
     */
    protected void afterExecute(AnalysisContext<T> context, AnalysisResult result) {
        log.info("[{}] analysis success, result={}", getTaskName(), result);
    }

    /**
     * 清理工作
     * <p>
     * 删除临时表
     * 删除临时索引
     * 清理中间结果
     */
    protected void cleanUp(AnalysisContext<T> context) {
    }

    /**
     * 异常处理
     */
    protected void onError(AnalysisContext<T> context, Exception e) {
        log.error("[{}] analysis failed, context={}", getTaskName(), context, e);
    }

    /**
     * 统计耗时
     */
    protected void logCost(long startTime) {
        long cost = System.currentTimeMillis() - startTime;
        log.info("[{}] analysis finished, cost={} ms", getTaskName(), cost);
    }

    /**
     * 默认任务名称
     */
    protected String getTaskName() {
        return getClass().getSimpleName();
    }

}