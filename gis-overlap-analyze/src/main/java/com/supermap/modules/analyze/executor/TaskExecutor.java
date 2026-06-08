package com.supermap.modules.analyze.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class TaskExecutor {

    @Bean("analyzeTaskExecutor")
    public ThreadPoolTaskExecutor analyzeTaskExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：GIS分析是CPU密集型任务，核心线程不超过CPU核数
        executor.setCorePoolSize(Math.max(2, cpuCores / 2));
        // 最大线程数：允许弹性扩展到CPU核数
        executor.setMaxPoolSize(cpuCores);
        // 有界队列：容量50，防止任务无限堆积
        executor.setQueueCapacity(50);
        // 空闲线程存活时间
        executor.setKeepAliveSeconds(60);
        // 线程名前缀，便于日志排查
        executor.setThreadNamePrefix("analyze-task-");
        // 拒绝策略：由调用线程执行，不丢弃任务，同时起到降速作用
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待任务完成后才关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    @Bean("uploadTaskExecutor")
    public ThreadPoolTaskExecutor uploadTaskExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 上传是IO密集型任务，核心线程可以多一些
        executor.setCorePoolSize(cpuCores);
        executor.setMaxPoolSize(cpuCores * 2);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("upload-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

}
