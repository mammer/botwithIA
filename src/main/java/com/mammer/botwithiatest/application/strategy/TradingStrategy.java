package com.mammer.botwithiatest.application.strategy;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;

import java.util.List;

public interface TradingStrategy {
    String getSymbol();

    TradeSignal evaluate(List<Candle> candles);
}
