package com.mammer.botwithiatest.application.strategy;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.SymbolConfig;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovingAverageCrossoverStrategy implements TradingStrategy {

    private static final int SHORT_WINDOW = 20;
    private static final int LONG_WINDOW = 50;

    private final SymbolConfig symbolConfig;

    public MovingAverageCrossoverStrategy(SymbolConfig symbolConfig) {
        this.symbolConfig = symbolConfig;
    }

    @Override
    public String getSymbol() {
        return symbolConfig.getSymbol();
    }

    @Override
    public TradeSignal evaluate(List<Candle> candles) {
        if (candles == null || candles.size() < LONG_WINDOW) {
            return TradeSignal.NONE;
        }

        double shortMa = simpleMovingAverage(candles, SHORT_WINDOW);
        double longMa = simpleMovingAverage(candles, LONG_WINDOW);

        if (shortMa > longMa) {
            return TradeSignal.BUY;
        }
        if (shortMa < longMa) {
            return TradeSignal.SELL;
        }
        return TradeSignal.NONE;
    }

    private double simpleMovingAverage(List<Candle> candles, int window) {
        return candles
                .subList(candles.size() - window, candles.size())
                .stream()
                .mapToDouble(Candle::getClose)
                .summaryStatistics()
                .getAverage();
    }
}
