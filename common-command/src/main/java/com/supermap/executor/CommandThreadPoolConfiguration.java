package com.supermap.executor;

import com.supermap.config.CommandProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 读取cmd输出是IO密集型任务，核心线程可以多一些
 */
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(CommandProperties.CommandExecutorProperties.class)
public class CommandThreadPoolConfiguration {

    @Bean
    public ThreadPoolTaskExecutor commandThreadPoolExecutor(CommandProperties.CommandExecutorProperties executorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setKeepAliveSeconds((int) executorProperties.getKeepAlive().getSeconds());
        executor.setThreadNamePrefix("command-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

}
