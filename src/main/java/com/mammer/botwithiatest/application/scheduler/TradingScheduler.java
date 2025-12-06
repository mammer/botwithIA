package com.mammer.botwithiatest.application.scheduler;

import com.mammer.botwithiatest.application.service.TradingService;
import com.mammer.botwithiatest.application.strategy.TradingStrategy;
import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.datasource.MarketDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TradingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TradingScheduler.class);

    private final MarketDataProvider marketDataProvider;
    private final TradingStrategy tradingStrategy;
    private final TradingService tradingService;

    public TradingScheduler(MarketDataProvider marketDataProvider,
                            TradingStrategy tradingStrategy,
                            TradingService tradingService) {
        this.marketDataProvider = marketDataProvider;
        this.tradingStrategy = tradingStrategy;
        this.tradingService = tradingService;
    }

    @Scheduled(fixedDelay = 60000)
    public void pollAndTrade() {
        String symbol = tradingStrategy.getSymbol();
        List<Candle> candles = marketDataProvider.fetchLatestCandles(symbol, 100);

        if (candles.isEmpty()) {
            logger.warn("No candles available for {}", symbol);
            return;
        }

        TradeSignal signal = tradingStrategy.evaluate(candles);
        if (signal == TradeSignal.NONE) {
            logger.info("No actionable signal for {}", symbol);
            return;
        }

        Candle last = candles.get(candles.size() - 1);
        double spread = Math.max(0.0, last.getHigh() - last.getLow());
        double stopLoss = signal == TradeSignal.BUY ? last.getClose() - 5 : last.getClose() + 5;
        double takeProfit = signal == TradeSignal.BUY ? last.getClose() + 10 : last.getClose() - 10;
        double stopLossPips = Math.abs(last.getClose() - stopLoss);

        logger.info("Executing {} signal for {} with SL {} and TP {}", signal, symbol, stopLoss, takeProfit);
        tradingService.placeMarketOrder(symbol, signal, last.getClose(), stopLoss, takeProfit, spread, 10_000, stopLossPips);
    }
}
