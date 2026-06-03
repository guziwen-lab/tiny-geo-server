package com.supermap.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzw
 */
@Slf4j
@Configuration
public class StartupConfig {

    @Bean
    public Tika tika() {
        return new Tika();
    }

}
