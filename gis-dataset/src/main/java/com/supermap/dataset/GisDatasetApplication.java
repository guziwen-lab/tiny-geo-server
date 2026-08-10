package com.supermap.dataset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.supermap")
public class GisDatasetApplication {

    public static void main(String[] args) {
        SpringApplication.run(GisDatasetApplication.class, args);
    }

}
