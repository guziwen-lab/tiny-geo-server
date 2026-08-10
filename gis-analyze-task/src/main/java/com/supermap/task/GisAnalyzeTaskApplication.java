package com.supermap.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.supermap")
public class GisAnalyzeTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(GisAnalyzeTaskApplication.class, args);
    }

}
