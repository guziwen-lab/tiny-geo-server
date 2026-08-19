package com.supermap.file.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzw
 */
@Configuration
public class TikaConfig {

    @Bean
    public Tika tika() {
        return new Tika();
    }

}
