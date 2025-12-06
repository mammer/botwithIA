package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.SymbolConfig;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TradingService {

    private final MT5BridgeClient bridgeClient;
    private final RiskManagementService riskManagementService;
    private final Map<String, SymbolConfig> symbolConfigs = new HashMap<>();

    public TradingService(MT5BridgeClient bridgeClient, RiskManagementService riskManagementService) {
        this.bridgeClient = bridgeClient;
        this.riskManagementService = riskManagementService;
        registerDefaultSymbols();
    }

    public void registerSymbols(List<SymbolConfig> configs) {
        configs.forEach(config -> symbolConfigs.put(config.getSymbol(), config));
    }

    public boolean placeMarketOrder(String symbol, TradeSignal direction, double entryPrice, double stopLoss, double takeProfit,
                                    double spread, double equity, double stopLossPips) {
        SymbolConfig config = requireSymbol(symbol);
        validateDirection(direction);
        validateSpread(spread, config);

        double lotSize = resolveLotSize(config, equity, stopLossPips);
        return bridgeClient.sendMarketOrder(symbol, direction.name(), lotSize, stopLoss, takeProfit, config.getSlippage());
    }

    public boolean placePendingOrder(String symbol, TradeSignal direction, double entryPrice, double stopLoss, double takeProfit,
                                     double spread, double equity, double stopLossPips) {
        SymbolConfig config = requireSymbol(symbol);
        validateDirection(direction);
        validateSpread(spread, config);

        double lotSize = resolveLotSize(config, equity, stopLossPips);
        return bridgeClient.sendPendingOrder(symbol, direction.name(), entryPrice, lotSize, stopLoss, takeProfit, config.getSlippage());
    }

    private SymbolConfig requireSymbol(String symbol) {
        SymbolConfig config = symbolConfigs.get(symbol);
        if (config == null) {
            throw new IllegalArgumentException("Symbol not configured: " + symbol);
        }
        return config;
    }

    private void validateSpread(double spread, SymbolConfig config) {
        if (spread > config.getMaxSpread()) {
            throw new IllegalStateException("Spread too high for " + config.getSymbol() + ": " + spread);
        }
    }

    private void validateDirection(TradeSignal direction) {
        if (direction == TradeSignal.NONE) {
            throw new IllegalArgumentException("Direction cannot be NONE for an order");
        }
    }

    private double resolveLotSize(SymbolConfig config, double equity, double stopLossPips) {
        if (equity > 0 && stopLossPips > 0) {
            return riskManagementService.computePositionSize(equity, stopLossPips, config.getRiskPerTrade());
        }
        return config.getLotSize();
    }

    private void registerDefaultSymbols() {
        registerSymbols(List.of(new SymbolConfig("XAUUSD", 0.1, 1.5, 0.5, 0.01)));
    }
}
