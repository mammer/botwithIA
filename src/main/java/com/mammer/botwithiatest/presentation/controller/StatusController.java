package com.mammer.botwithiatest.presentation.controller;

import com.mammer.botwithiatest.infrastructure.config.AppModeService;
import com.mammer.botwithiatest.presentation.dto.StatusResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private final AppModeService appModeService;

    public StatusController(AppModeService appModeService) {
        this.appModeService = appModeService;
    }

    @GetMapping("/status")
    public StatusResponseDTO status() {
        return new StatusResponseDTO(
                appModeService.getCurrentMode(),
                appModeService.getStartTime(),
                appModeService.getDemoExpiry(),
                appModeService.isDemoExpired()
        );
    }
}
