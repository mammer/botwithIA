package com.mammer.botwithiatest.application.usecase;

import com.mammer.botwithiatest.application.service.FeatureExtractionService;
import com.mammer.botwithiatest.application.service.TradingEngine;
import com.mammer.botwithiatest.application.strategy.StrategyEngine;
import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.MarketTrend;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.datasource.MarketDataProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ExecuteTradeUseCase {

    private final MarketDataProvider marketDataProvider;
    private final StrategyEngine strategyEngine;
    private final TradingEngine tradingEngine;
    private final FeatureExtractionService featureExtractionService;

    public ExecuteTradeUseCase(MarketDataProvider marketDataProvider,
                               StrategyEngine strategyEngine,
                               TradingEngine tradingEngine,
                               FeatureExtractionService featureExtractionService) {
        this.marketDataProvider = marketDataProvider;
        this.strategyEngine = strategyEngine;
        this.tradingEngine = tradingEngine;
        this.featureExtractionService = featureExtractionService;
    }

    public void execute(String symbol, MarketTrend trend, double equity, double stopLossPips, double stopLossPrice, double takeProfitPrice) {
        List<Candle> candles = marketDataProvider.fetchLatestCandles(symbol, 200);
        Map<String, Double> features = featureExtractionService.extractLatestFeatures(candles);
        TradeSignal signal = strategyEngine.generateFinalSignal(candles, trend, features);
        tradingEngine.executeTrade(signal, equity, stopLossPips, stopLossPrice, takeProfitPrice);
    }
}
