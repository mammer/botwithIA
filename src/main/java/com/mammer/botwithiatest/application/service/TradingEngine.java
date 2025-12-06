package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.exception.DomainException;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.config.AppMode;
import com.mammer.botwithiatest.infrastructure.config.ApplicationStatusService;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;
import org.springframework.stereotype.Service;

@Service
public class TradingEngine {

    private final MT5BridgeClient bridgeClient;
    private final RiskManagementService riskManagementService;
    private final ApplicationStatusService applicationStatusService;

    public TradingEngine(MT5BridgeClient bridgeClient, RiskManagementService riskManagementService, ApplicationStatusService applicationStatusService) {
        this.bridgeClient = bridgeClient;
        this.riskManagementService = riskManagementService;
        this.applicationStatusService = applicationStatusService;
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

        if (shouldBlockOrders(signal)) {
            throw new IllegalStateException("Demo period expired; live order placement is blocked.");
        }

        switch (signal) {
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

    private boolean shouldBlockOrders(TradeSignal signal) {
        return applicationStatusService.getCurrentMode() == AppMode.DEMO
                && applicationStatusService.isDemoExpired()
                && signal != TradeSignal.NONE;
    }
}
