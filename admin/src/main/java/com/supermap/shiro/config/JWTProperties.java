package com.supermap.shiro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gzw
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {

    private String secret;

    private Integer expire;

    private String header;

}
