package com.mammer.botwithiatest.application.strategy;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovingAverageCrossoverStrategy implements TradingStrategy {

    private static final String SYMBOL = "XAUUSD";
    private final int shortPeriod;
    private final int longPeriod;

    public MovingAverageCrossoverStrategy() {
        this(10, 30);
    }

    public MovingAverageCrossoverStrategy(int shortPeriod, int longPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

    @Override
    public TradeSignal evaluate(List<Candle> candles) {
        if (candles.size() < longPeriod) {
            return TradeSignal.NONE;
        }

        double shortMA = calculateSma(candles, shortPeriod);
        double longMA = calculateSma(candles, longPeriod);

        if (shortMA > longMA) {
            return TradeSignal.BUY;
        }
        if (shortMA < longMA) {
            return TradeSignal.SELL;
        }
        return TradeSignal.NONE;
    }

    private double calculateSma(List<Candle> candles, int period) {
        int startIndex = candles.size() - period;
        double sum = 0;
        for (int i = startIndex; i < candles.size(); i++) {
            sum += candles.get(i).getClose();
        }
        return sum / period;
    }
}
