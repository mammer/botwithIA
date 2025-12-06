package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;
import org.springframework.stereotype.Service;

@Service
public class TradingEngine {

    private final MT5BridgeClient bridgeClient;
    private final RiskManagementService riskManagementService;

    public TradingEngine(MT5BridgeClient bridgeClient, RiskManagementService riskManagementService) {
        this.bridgeClient = bridgeClient;
        this.riskManagementService = riskManagementService;
    }

    public boolean executeTrade(TradeSignal signal, double equity, double stopLossPips, double stopLossPrice, double takeProfitPrice) {
        validateInputs(signal, equity, stopLossPips, stopLossPrice, takeProfitPrice);

        double lotSize = riskManagementService.computePositionSize(equity, stopLossPips);
        if (!riskManagementService.isWithinRiskLimits(equity, stopLossPips, lotSize)) {
            return false;
        }

        if (!bridgeClient.isConnected()) {
            return false;
        }

        return switch (signal) {
            case BUY -> bridgeClient.sendOrder("BUY", lotSize, stopLossPrice, takeProfitPrice);
            case SELL -> bridgeClient.sendOrder("SELL", lotSize, stopLossPrice, takeProfitPrice);
            case NONE -> bridgeClient.sendOrder("CLOSE", 0, 0, 0);
        };
    }

    private void validateInputs(TradeSignal signal, double equity, double stopLossPips, double stopLossPrice, double takeProfitPrice) {
        if (signal == null) {
            throw new IllegalArgumentException("Trade signal cannot be null");
        }
        if (equity <= 0) {
            throw new IllegalArgumentException("Equity must be greater than zero");
        }
        if (stopLossPips <= 0 || stopLossPrice <= 0 || takeProfitPrice <= 0) {
            throw new IllegalArgumentException("Risk parameters must be greater than zero");
        }
    }
}
