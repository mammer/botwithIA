package com.mammer.botwithiatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BotWithIaTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotWithIaTestApplication.class, args);
    }

}
