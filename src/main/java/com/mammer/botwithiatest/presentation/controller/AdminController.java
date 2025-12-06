package com.mammer.botwithiatest.presentation.controller;

import com.mammer.botwithiatest.infrastructure.config.AppMode;
import com.mammer.botwithiatest.infrastructure.config.AppModeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final AppModeService appModeService;
    private final String adminToken;

    public AdminController(AppModeService appModeService,
                           @Value("${app.admin.token:change-me}") String adminToken) {
        this.appModeService = appModeService;
        this.adminToken = adminToken;
    }

    @PostMapping("/admin/mode")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void switchMode(@RequestHeader("X-Admin-Token") String token,
                           @RequestParam("mode") String mode) {
        validateToken(token);
        AppMode targetMode = AppMode.valueOf(mode.toUpperCase());
        appModeService.setMode(targetMode);
    }

    private void validateToken(String token) {
        if (!adminToken.equals(token)) {
            throw new UnauthorizedAdminAction("Invalid admin token");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private static class UnauthorizedAdminAction extends RuntimeException {
        public UnauthorizedAdminAction(String message) {
            super(message);
        }
    }
}
