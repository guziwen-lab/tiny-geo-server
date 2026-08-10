package com.supermap.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.supermap")
public class GisPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(GisPlatformApplication.class, args);
    }

}
