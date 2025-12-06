package com.mammer.botwithiatest.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AppModeService {

    private final Instant startTime;
    private final Duration demoDuration;
    private volatile AppMode currentMode;

    public AppModeService(@Value("${app.mode:DEMO}") String initialMode,
                          @Value("${app.demo.duration-days:5}") long demoDurationDays) {
        this.startTime = Instant.now();
        this.currentMode = AppMode.valueOf(initialMode.toUpperCase());
        this.demoDuration = Duration.ofDays(demoDurationDays);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public AppMode getCurrentMode() {
        return currentMode;
    }

    public synchronized void setMode(AppMode mode) {
        this.currentMode = mode;
    }

    public Instant getDemoExpiry() {
        return startTime.plus(demoDuration);
    }

    public boolean isDemoExpired() {
        return currentMode == AppMode.DEMO && Instant.now().isAfter(getDemoExpiry());
    }

    public boolean canPlaceLiveOrders() {
        if (currentMode == AppMode.DEMO && isDemoExpired()) {
            return false;
        }
        return true;
    }
}
