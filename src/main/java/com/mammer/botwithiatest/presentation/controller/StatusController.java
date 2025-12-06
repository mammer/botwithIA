package com.mammer.botwithiatest.presentation.controller;

import com.mammer.botwithiatest.infrastructure.config.AppMode;
import com.mammer.botwithiatest.infrastructure.config.ApplicationStatusService;
import com.mammer.botwithiatest.presentation.dto.ModeUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/status")
public class StatusController {

    private final ApplicationStatusService applicationStatusService;

    public StatusController(ApplicationStatusService applicationStatusService) {
        this.applicationStatusService = applicationStatusService;
    }

    @GetMapping
    public Map<String, Object> status() {
        Instant startedAt = applicationStatusService.getStartedAt();
        return Map.of(
                "mode", applicationStatusService.getCurrentMode(),
                "startedAt", startedAt,
                "demoExpiresAt", applicationStatusService.getDemoExpiryAt(),
                "demoExpired", applicationStatusService.isDemoExpired()
        );
    }

    @PostMapping("/mode")
    public ResponseEntity<Map<String, Object>> updateMode(@RequestHeader(value = "X-Admin-Token", required = false) String token,
                                                          @RequestBody ModeUpdateRequest request) {
        if (!applicationStatusService.isAuthorized(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AppMode newMode = request.mode();
        if (newMode == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mode is required"));
        }

        applicationStatusService.updateMode(newMode);
        return ResponseEntity.ok(Map.of("mode", applicationStatusService.getCurrentMode()));
    }
}
