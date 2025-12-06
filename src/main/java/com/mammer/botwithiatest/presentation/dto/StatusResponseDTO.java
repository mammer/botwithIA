package com.mammer.botwithiatest.presentation.dto;

import com.mammer.botwithiatest.infrastructure.config.AppMode;

import java.time.Instant;

public record StatusResponseDTO(AppMode mode, Instant startedAt, Instant demoExpiry, boolean demoExpired) {
}
