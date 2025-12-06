package com.mammer.botwithiatest.infrastructure.mt5;

import org.springframework.stereotype.Component;

@Component
public class MT5BridgeClient {

    private boolean connected = true;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean sendOrder(String direction, double lotSize, double sl, double tp) {
        // later: HTTP to MT5 EA or local socket
        System.out.println("Sending order to MT5: " + direction);
        return true;
    }
}
