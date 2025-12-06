package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.domaine.exception.DomainException;
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

    public void executeTrade(TradeSignal signal, double equity, double stopLossPips, double stopLossPrice, double takeProfitPrice) {
        if (signal == null) {
            throw new DomainException("Trade signal must be provided");
        }

        if (!bridgeClient.isConnected()) {
            throw new DomainException("MT5 bridge is disconnected; refusing to place order");
        }

        if ((signal == TradeSignal.BUY || signal == TradeSignal.SELL)) {
            validatePriceLevels(stopLossPrice, takeProfitPrice);
            double lotSize = riskManagementService.computePositionSize(equity, stopLossPips);

            switch (signal) {
                case BUY -> bridgeClient.sendOrder("BUY", lotSize, stopLossPrice, takeProfitPrice);
                case SELL -> bridgeClient.sendOrder("SELL", lotSize, stopLossPrice, takeProfitPrice);
                default -> throw new DomainException("Unsupported trade signal");
            }
            return;
        }

        bridgeClient.sendOrder("CLOSE", 0, 0, 0);
    }

    private void validatePriceLevels(double stopLossPrice, double takeProfitPrice) {
        if (stopLossPrice <= 0 || Double.isNaN(stopLossPrice) || Double.isInfinite(stopLossPrice)) {
            throw new DomainException("Stop loss price must be a positive number");
        }

        if (takeProfitPrice <= 0 || Double.isNaN(takeProfitPrice) || Double.isInfinite(takeProfitPrice)) {
            throw new DomainException("Take profit price must be a positive number");
        }
    }
}
