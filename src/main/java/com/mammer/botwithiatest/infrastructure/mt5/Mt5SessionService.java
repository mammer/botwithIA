package com.mammer.botwithiatest.infrastructure.mt5;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class Mt5SessionService {

    private final Mt5Client mt5Client;
    private final Mt5Properties mt5Properties;

    public Mt5SessionService(Mt5Client mt5Client, Mt5Properties mt5Properties) {
        this.mt5Client = mt5Client;
        this.mt5Properties = mt5Properties;
    }

    public Mt5SessionStatus connect() {
        if (!mt5Properties.isFullyConfigured()) {
            return new Mt5SessionStatus(false, null, null, null, null,
                    "MT5 credentials are not fully configured. Set environment variables before connecting.");
        }

        if (!mt5Client.isConnected()) {
            mt5Client.login(mt5Properties.getServer(), mt5Properties.getLogin(), mt5Properties.getPassword(),
                    mt5Properties.getAccountType());
        }

        return status("Connected to MT5");
    }

    public Mt5SessionStatus status() {
        if (!mt5Properties.isFullyConfigured()) {
            return new Mt5SessionStatus(false, null, null, null, null,
                    "MT5 credentials are not fully configured. Set environment variables before connecting.");
        }
        return status(mt5Client.isConnected() ? "Connected to MT5" : "MT5 session not connected");
    }

    public Mt5SessionStatus disconnect() {
        mt5Client.logout();
        return new Mt5SessionStatus(false, mt5Properties.getServer(), mt5Properties.getLogin(),
                mt5Properties.getAccountType(), null, "Disconnected from MT5");
    }

    private Mt5SessionStatus status(String message) {
        String server = mt5Properties.getServer();
        String login = mt5Properties.getLogin();
        Mt5AccountType accountType = mt5Properties.getAccountType();

        var context = mt5Client.getConnectionContext();
        return new Mt5SessionStatus(mt5Client.isConnected(), server, login, accountType,
                context.map(Mt5Client.Mt5ConnectionContext::connectedAt).orElse(null),
                buildMessage(message, context.isPresent()));
    }

    private String buildMessage(String baseMessage, boolean hasContext) {
        if (!StringUtils.hasText(baseMessage)) {
            return null;
        }
        if (hasContext) {
            return baseMessage;
        }
        return baseMessage + " (no active session)";
    }
}
