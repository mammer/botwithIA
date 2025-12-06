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
        return sendMarketOrder("", direction, lotSize, sl, tp, 0);
    }

    public boolean sendMarketOrder(String symbol, String direction, double lotSize, double stopLoss, double takeProfit, double slippage) {
        System.out.printf("[MT5] Market order %s %s lots %.2f SL %.2f TP %.2f slippage %.1f%n",
                symbol, direction, lotSize, stopLoss, takeProfit, slippage);
        return true;
    }

    public boolean sendPendingOrder(String symbol, String direction, double entryPrice, double lotSize, double stopLoss, double takeProfit, double slippage) {
        System.out.printf("[MT5] Pending order %s %s @ %.2f lots %.2f SL %.2f TP %.2f slippage %.1f%n",
                symbol, direction, entryPrice, lotSize, stopLoss, takeProfit, slippage);
        return true;
    }
}
