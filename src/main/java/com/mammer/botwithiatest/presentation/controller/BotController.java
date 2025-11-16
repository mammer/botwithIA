package com.mammer.botwithiatest.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BotController {
    @GetMapping("/health")
    public String health() {
        return "Bot is running";
    }
}
