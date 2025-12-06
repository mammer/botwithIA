package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.exception.DomainException;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.config.AppModeService;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;
import org.springframework.stereotype.Service;

@Service
public class TradingEngine {

    private final MT5BridgeClient bridgeClient;
    private final RiskManagementService riskManagementService;
    private final AppModeService appModeService;

    public TradingEngine(MT5BridgeClient bridgeClient,
                         RiskManagementService riskManagementService,
                         AppModeService appModeService) {
        this.bridgeClient = bridgeClient;
        this.riskManagementService = riskManagementService;
        this.appModeService = appModeService;
    }

    public void executeTrade(TradeSignal signal, double equity, double stopLossPips, double stopLossPrice, double takeProfitPrice) {
        if (!appModeService.canPlaceLiveOrders()) {
            throw new DomainException("Demo period expired. Live trading is disabled.");
        }

        double lotSize = riskManagementService.computePositionSize(equity, stopLossPips);

        switch (signal) {
            case BUY -> bridgeClient.sendOrder("BUY", lotSize, stopLossPrice, takeProfitPrice);
            case SELL -> bridgeClient.sendOrder("SELL", lotSize, stopLossPrice, takeProfitPrice);
            case NONE -> bridgeClient.sendOrder("CLOSE", 0, 0, 0);
        }
    }
}
