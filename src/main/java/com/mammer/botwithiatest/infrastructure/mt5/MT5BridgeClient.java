package com.mammer.botwithiatest.infrastructure.mt5;

import org.springframework.stereotype.Component;

@Component
public class MT5BridgeClient {
    public boolean sendOrder(String direction, double lotSize, double sl, double tp) {
        return sendMarketOrder("", direction, lotSize, sl, tp, 0);
    }

    public boolean sendMarketOrder(String symbol, String direction, double lotSize, double sl, double tp, double slippage) {
        // later: HTTP to MT5 EA or local socket
        System.out.println("Sending market order to MT5: " + direction + " " + symbol);
        return true;
    }

    public boolean sendPendingOrder(String symbol, String direction, double entryPrice, double lotSize, double sl, double tp, double slippage) {
        // later: HTTP to MT5 EA or local socket
        System.out.println("Sending pending order to MT5: " + direction + " @ " + entryPrice + " for " + symbol);
        return true;
    }
}
