package com.supermap.shiro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gzw
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sm3")
public class SM3KeyProperties {

    private String key = "G^cdmGop2972nn75";

}
