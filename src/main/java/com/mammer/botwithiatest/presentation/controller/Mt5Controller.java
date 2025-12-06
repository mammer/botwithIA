package com.mammer.botwithiatest.presentation.controller;

import com.mammer.botwithiatest.infrastructure.mt5.Mt5SessionService;
import com.mammer.botwithiatest.infrastructure.mt5.Mt5SessionStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mt5")
public class Mt5Controller {

    private final Mt5SessionService sessionService;

    public Mt5Controller(Mt5SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/connect")
    public ResponseEntity<Mt5SessionStatus> connect() {
        return ResponseEntity.ok(sessionService.connect());
    }

    @GetMapping("/status")
    public ResponseEntity<Mt5SessionStatus> status() {
        return ResponseEntity.ok(sessionService.status());
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Mt5SessionStatus> disconnect() {
        return ResponseEntity.ok(sessionService.disconnect());
    }
}
