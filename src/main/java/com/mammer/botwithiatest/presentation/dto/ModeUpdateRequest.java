package com.mammer.botwithiatest.presentation.dto;

import com.mammer.botwithiatest.infrastructure.config.AppMode;

public record ModeUpdateRequest(AppMode mode) {
}
