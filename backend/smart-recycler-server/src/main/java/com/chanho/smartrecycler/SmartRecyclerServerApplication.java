package com.chanho.smartrecycler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SmartRecyclerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartRecyclerServerApplication.class, args);
    }
}
