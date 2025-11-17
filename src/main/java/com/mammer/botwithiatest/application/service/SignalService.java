package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.util.List;

@Service
public class SignalService {

    private static final int FAST_EMA = 50;
    private static final int SLOW_EMA = 200;
    private static final int RSI_PERIOD = 14;
    private static final int ATR_PERIOD = 14;

    /**
     * Main method: given a BarSeries (XAUUSD 15m, EURUSD, etc.), decide BUY / SELL / NONE.
     */
    public TradeSignal generateSignal(List<Candle> series) {
        // Safety: not enough candles → no trade
        if (series == null || series.getBarCount() < SLOW_EMA + ATR_PERIOD + 5) {
            return TradeSignal.NONE;
        }

        int endIndex = series.getEndIndex();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        EMAIndicator fastEma = new EMAIndicator(closePrice, FAST_EMA);
        EMAIndicator slowEma = new EMAIndicator(closePrice, SLOW_EMA);
        RSIIndicator rsi = new RSIIndicator(closePrice, RSI_PERIOD);
        ATRIndicator atr = new ATRIndicator(series, ATR_PERIOD); // <-- replaces TrueRangeIndicator

        Num fast = fastEma.getValue(endIndex);
        Num slow = slowEma.getValue(endIndex);
        Num rsiValue = rsi.getValue(endIndex);
        Num atrValue = atr.getValue(endIndex); // we can pass this later to RiskManagementService

        boolean upTrend = fast.isGreaterThan(slow);
        boolean downTrend = fast.isLessThan(slow);

        // Very simple base logic (we will refine later with LiquiditySweepDetector etc.)
        // BUY: in uptrend and RSI indicates a pullback
        if (upTrend && rsiValue.isLessThan(series.numOf(40))) {
            return TradeSignal.BUY;
        }

        // SELL: in downtrend and RSI indicates a pullback
        if (downTrend && rsiValue.isGreaterThan(series.numOf(60))) {
            return TradeSignal.SELL;
        }

        return TradeSignal.NONE;
    }

    /**
     * Utility: get current ATR value (for SL/TP and risk management).
     */
    public double getCurrentAtr(BarSeries series) {
        if (series == null || series.getBarCount() < ATR_PERIOD + 1) {
            return 0.0;
        }
        ATRIndicator atr = new ATRIndicator(series, ATR_PERIOD);
        Num value = atr.getValue(series.getEndIndex());
        return value.doubleValue();
    }
}
