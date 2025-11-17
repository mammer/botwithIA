package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    public TradeSignal generateSignal(List<Candle> candles) {
        if (candles == null || candles.size() < SLOW_EMA + ATR_PERIOD + 5) {
            return TradeSignal.NONE;
        }

        BarSeries series = toBarSeries(candles);

        if (series.getBarCount() < SLOW_EMA + ATR_PERIOD + 5) {
            return TradeSignal.NONE;
        }

        int endIndex = series.getEndIndex();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        EMAIndicator fastEma = new EMAIndicator(closePrice, FAST_EMA);
        EMAIndicator slowEma = new EMAIndicator(closePrice, SLOW_EMA);
        RSIIndicator rsi = new RSIIndicator(closePrice, RSI_PERIOD);
        ATRIndicator atr = new ATRIndicator(series, ATR_PERIOD);

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

    private BarSeries toBarSeries(List<Candle> candles) {
        Duration barDuration = candles.size() > 1
                ? Duration.between(candles.get(0).getTimestamp(), candles.get(1).getTimestamp())
                : Duration.ofMinutes(1);

        if (barDuration.isNegative() || barDuration.isZero()) {
            barDuration = Duration.ofMinutes(1);
        }

        BarSeries series = new BaseBarSeriesBuilder().withName("candles").build();

        for (Candle candle : candles) {
            ZonedDateTime endTime = ZonedDateTime.of(candle.getTimestamp(), ZoneId.systemDefault());
            series.addBar(new BaseBar(barDuration, endTime,
                    candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume()));
        }

        return series;
    }
}
