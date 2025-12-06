package com.mammer.botwithiatest.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ApplicationStatusService {

    private static final Duration DEMO_DURATION = Duration.ofDays(5);

    private final Instant startedAt;
    private final AtomicReference<AppMode> currentMode;
    private final String adminToken;

    public ApplicationStatusService(@Value("${app.mode:DEMO}") String mode,
                                    @Value("${app.admin.token:changeme}") String adminToken) {
        this.startedAt = Instant.now();
        this.currentMode = new AtomicReference<>(parseMode(mode));
        this.adminToken = adminToken;
    }

    private AppMode parseMode(String value) {
        try {
            return AppMode.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            return AppMode.DEMO;
        }
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public AppMode getCurrentMode() {
        return currentMode.get();
    }

    public void updateMode(AppMode mode) {
        currentMode.set(mode);
    }

    public Instant getDemoExpiryAt() {
        return startedAt.plus(DEMO_DURATION);
    }

    public boolean isDemoExpired() {
        return Instant.now().isAfter(getDemoExpiryAt());
    }

    public boolean isAuthorized(String providedToken) {
        return providedToken != null && !providedToken.isBlank() && providedToken.equals(adminToken);
    }
}
