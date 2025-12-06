package com.mammer.botwithiatest.infrastructure.mt5;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

@Component
public class Mt5Client {

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private volatile Mt5ConnectionContext connectionContext;

    public boolean login(String server, String login, String password, Mt5AccountType accountType) {
        if (!connected.compareAndSet(false, true)) {
            return true;
        }

        connectionContext = new Mt5ConnectionContext(server, login, accountType, Instant.now());
        return true;
    }

    public void logout() {
        connected.set(false);
        connectionContext = null;
    }

    public boolean isConnected() {
        return connected.get();
    }

    public Optional<Mt5ConnectionContext> getConnectionContext() {
        return Optional.ofNullable(connectionContext);
    }

    public record Mt5ConnectionContext(String server, String login, Mt5AccountType accountType, Instant connectedAt) {
    }
}
