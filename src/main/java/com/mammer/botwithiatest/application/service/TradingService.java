package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.SymbolConfig;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.datasource.MarketDataProvider;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;
import org.springframework.stereotype.Service;

@Service
public class TradingService {

    private final MT5BridgeClient bridgeClient;
    private final MarketDataProvider marketDataProvider;
    private final SymbolConfig symbolConfig;

    public TradingService(MT5BridgeClient bridgeClient, MarketDataProvider marketDataProvider, SymbolConfig symbolConfig) {
        this.bridgeClient = bridgeClient;
        this.marketDataProvider = marketDataProvider;
        this.symbolConfig = symbolConfig;
    }

    public boolean placeMarketOrder(TradeSignal direction, double stopLoss, double takeProfit) {
        validateSymbol(symbolConfig.getSymbol());
        validateSpread();
        return bridgeClient.sendMarketOrder(
                symbolConfig.getSymbol(),
                direction.name(),
                symbolConfig.getLotSize(),
                stopLoss,
                takeProfit,
                symbolConfig.getSlippage());
    }

    public boolean placePendingOrder(TradeSignal direction, double entryPrice, double stopLoss, double takeProfit) {
        validateSymbol(symbolConfig.getSymbol());
        validateSpread();
        return bridgeClient.sendPendingOrder(
                symbolConfig.getSymbol(),
                direction.name(),
                entryPrice,
                symbolConfig.getLotSize(),
                stopLoss,
                takeProfit,
                symbolConfig.getSlippage());
    }

    private void validateSymbol(String symbol) {
        if (!symbolConfig.getSymbol().equalsIgnoreCase(symbol)) {
            throw new IllegalArgumentException("Symbol " + symbol + " not supported by current configuration");
        }
    }

    private void validateSpread() {
        double spread = marketDataProvider.fetchCurrentSpread(symbolConfig.getSymbol());
        if (spread > symbolConfig.getMaxSpread()) {
            throw new IllegalStateException("Spread too wide to place order: " + spread);
        }
    }
}
