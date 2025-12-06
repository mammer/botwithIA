package com.mammer.botwithiatest.application.scheduler;

import com.mammer.botwithiatest.application.service.TradingService;
import com.mammer.botwithiatest.application.strategy.TradingStrategy;
import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.SymbolConfig;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.datasource.MarketDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradingScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingScheduler.class);

    private final MarketDataProvider marketDataProvider;
    private final TradingStrategy tradingStrategy;
    private final TradingService tradingService;
    private final SymbolConfig symbolConfig;

    public TradingScheduler(MarketDataProvider marketDataProvider,
                            TradingStrategy tradingStrategy,
                            TradingService tradingService,
                            SymbolConfig symbolConfig) {
        this.marketDataProvider = marketDataProvider;
        this.tradingStrategy = tradingStrategy;
        this.tradingService = tradingService;
        this.symbolConfig = symbolConfig;
    }

    @Scheduled(fixedDelayString = "${trading.poll-interval-ms:60000}")
    public void pollMarketAndTrade() {
        List<Candle> candles = marketDataProvider.fetchLatestCandles(symbolConfig.getSymbol(), 200);
        if (candles.isEmpty()) {
            LOGGER.info("No candle data available for {}. Skipping.", symbolConfig.getSymbol());
            return;
        }

        TradeSignal signal = tradingStrategy.evaluate(candles);
        if (signal == TradeSignal.NONE) {
            LOGGER.info("Strategy returned no actionable signal for {}", symbolConfig.getSymbol());
            return;
        }

        double lastClose = candles.get(candles.size() - 1).getClose();
        double averageRange = estimateAverageRange(candles, 14);
        double stopLoss = signal == TradeSignal.BUY ? lastClose - averageRange : lastClose + averageRange;
        double takeProfit = signal == TradeSignal.BUY ? lastClose + (averageRange * 2) : lastClose - (averageRange * 2);

        LOGGER.info("Placing {} market order for {} with SL {} and TP {}", signal, symbolConfig.getSymbol(), stopLoss, takeProfit);
        tradingService.placeMarketOrder(signal, stopLoss, takeProfit);
    }

    private double estimateAverageRange(List<Candle> candles, int lookback) {
        int startIndex = Math.max(candles.size() - lookback, 0);
        double sum = 0;
        int count = 0;
        for (int i = startIndex; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            sum += candle.getHigh() - candle.getLow();
            count++;
        }
        double averageRange = count == 0 ? 0 : sum / count;
        return averageRange == 0 ? 1.0 : averageRange;
    }
}
