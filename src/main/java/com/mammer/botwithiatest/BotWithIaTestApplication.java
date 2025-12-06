package com.mammer.botwithiatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BotWithIaTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotWithIaTestApplication.class, args);
    }

}
