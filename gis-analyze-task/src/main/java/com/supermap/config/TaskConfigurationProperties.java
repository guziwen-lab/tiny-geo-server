package com.supermap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@Component
@ConfigurationProperties(prefix = "task")
@Data
public class TaskConfigurationProperties {

    /**
     * 结果表的主键列名
     */
    private String pkColumnName = "serial_id";

}
