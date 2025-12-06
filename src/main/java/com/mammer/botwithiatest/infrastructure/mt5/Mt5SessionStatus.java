package com.mammer.botwithiatest.infrastructure.mt5;

import java.time.Instant;

public record Mt5SessionStatus(boolean connected, String server, String login, Mt5AccountType accountType, Instant connectedAt,
                               String message) {
}
