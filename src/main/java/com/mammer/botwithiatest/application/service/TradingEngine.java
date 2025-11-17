package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;

public class TradingEngine {

    private final MT5BridgeClient bridgeClient;
    private final RiskManagementService riskManagementService;

    public TradingEngine(MT5BridgeClient bridgeClient, RiskManagementService riskManagementService) {
        this.bridgeClient = bridgeClient;
        this.riskManagementService = riskManagementService;
    }

    public void executeTrade(TradeSignal signal, double equity, double stopLossPips, double stopLossPrice, double takeProfitPrice) {
        double lotSize = riskManagementService.computePositionSize(equity, stopLossPips);

        switch (signal) {
            case BUY -> bridgeClient.sendOrder("BUY", lotSize, stopLossPrice, takeProfitPrice);
            case SELL -> bridgeClient.sendOrder("SELL", lotSize, stopLossPrice, takeProfitPrice);
            case NONE -> bridgeClient.sendOrder("CLOSE", 0, 0, 0);
        }
    }
}
