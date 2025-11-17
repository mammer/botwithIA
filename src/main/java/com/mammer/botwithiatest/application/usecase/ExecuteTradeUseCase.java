package com.mammer.botwithiatest.application.usecase;

import com.mammer.botwithiatest.application.service.TradingEngine;
import com.mammer.botwithiatest.application.strategy.StrategyEngine;
import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.MarketTrend;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.datasource.MarketDataProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteTradeUseCase {

    private final MarketDataProvider marketDataProvider;
    private final StrategyEngine strategyEngine;
    private final TradingEngine tradingEngine;

    public ExecuteTradeUseCase(MarketDataProvider marketDataProvider,
                               StrategyEngine strategyEngine,
                               TradingEngine tradingEngine) {
        this.marketDataProvider = marketDataProvider;
        this.strategyEngine = strategyEngine;
        this.tradingEngine = tradingEngine;
    }

    public void execute(String symbol, MarketTrend trend, double equity, double stopLossPips, double stopLossPrice, double takeProfitPrice) {
        List<Candle> candles = marketDataProvider.fetchLatestCandles(symbol, 200);
        Map<String, Double> features = buildRealtimeFeatures(candles);
        TradeSignal signal = strategyEngine.generateFinalSignal(candles, trend, features);
        tradingEngine.executeTrade(signal, equity, stopLossPips, stopLossPrice, takeProfitPrice);
    }

    private Map<String, Double> buildRealtimeFeatures(List<Candle> candles) {
        Map<String, Double> features = new HashMap<>();
        if (candles == null || candles.isEmpty()) {
            return features;
        }

        Candle last = candles.get(candles.size() - 1);
        features.put("last_close", last.getClose());
        features.put("last_high", last.getHigh());
        features.put("last_low", last.getLow());
        return features;
    }
}
