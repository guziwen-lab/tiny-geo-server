package com.supermap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gzw
 */
@ConfigurationProperties(prefix = "thread")
@Data
public class ThreadPoolProperties {

    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();
    private Integer maximumPoolSize = Runtime.getRuntime().availableProcessors();
    private Integer keepAliveTime = 10000;

}
