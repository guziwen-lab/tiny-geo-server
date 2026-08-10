package com.supermap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author gzw
 */
@ConfigurationProperties(prefix = "command")
@Getter
@Setter
public class CommandProperties {

    private Duration commandTimeout = Duration.ofMinutes(30);

    @ConfigurationProperties(prefix = "command.executor")
    @Getter
    @Setter
    public static class CommandExecutorProperties {

        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        private int queueCapacity = 100;
        private Duration keepAlive = Duration.ofSeconds(60);

    }

}
