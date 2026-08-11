package com.supermap.idgenerator.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 修改扫描的包路径，防止idea提示错误
 * @author gzw
 */
@Configuration
@ComponentScan(basePackages = "com.supermap")
public class IdGeneratorConfig {

}
