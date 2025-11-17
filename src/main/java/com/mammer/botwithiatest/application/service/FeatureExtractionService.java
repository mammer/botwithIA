package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.Candle;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeatureExtractionService {

    private static final int FAST_EMA = 50;
    private static final int SLOW_EMA = 200;
    private static final int RSI_PERIOD = 14;
    private static final int ATR_PERIOD = 14;

    /**
     * Build a small, easy-to-debug feature map for the latest candle.
     */
    public Map<String, Double> extractLatestFeatures(List<Candle> candles) {
        Map<String, Double> features = new HashMap<>();

        if (candles == null || candles.isEmpty()) {
            return features;
        }

        BarSeries series = toBarSeries(candles);
        if (series.getBarCount() < Math.max(SLOW_EMA, RSI_PERIOD) + 1) {
            // Not enough history to compute indicators
            Candle last = candles.get(candles.size() - 1);
            features.put("close", last.getClose());
            features.put("high", last.getHigh());
            features.put("low", last.getLow());
            features.put("volume", last.getVolume());
            return features;
        }

        int endIndex = series.getEndIndex();
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        EMAIndicator fastEma = new EMAIndicator(closePrice, FAST_EMA);
        EMAIndicator slowEma = new EMAIndicator(closePrice, SLOW_EMA);
        RSIIndicator rsi = new RSIIndicator(closePrice, RSI_PERIOD);
        ATRIndicator atr = new ATRIndicator(series, ATR_PERIOD);

        double close = series.getBar(endIndex).getClosePrice().doubleValue();
        double high = series.getBar(endIndex).getHighPrice().doubleValue();
        double low = series.getBar(endIndex).getLowPrice().doubleValue();

        features.put("close", close);
        features.put("high", high);
        features.put("low", low);
        features.put("volume", series.getBar(endIndex).getVolume().doubleValue());
        features.put("ema_fast", fastEma.getValue(endIndex).doubleValue());
        features.put("ema_slow", slowEma.getValue(endIndex).doubleValue());
        features.put("ema_spread", fastEma.getValue(endIndex).minus(slowEma.getValue(endIndex)).doubleValue());
        features.put("rsi", rsi.getValue(endIndex).doubleValue());
        features.put("atr", atr.getValue(endIndex).doubleValue());
        features.put("candle_body", close - series.getBar(endIndex).getOpenPrice().doubleValue());
        features.put("candle_range", high - low);

        return features;
    }

    private BarSeries toBarSeries(List<Candle> candles) {
        Duration barDuration = candles.size() > 1
                ? Duration.between(candles.get(0).getTimestamp(), candles.get(1).getTimestamp())
                : Duration.ofMinutes(1);

        if (barDuration.isNegative() || barDuration.isZero()) {
            barDuration = Duration.ofMinutes(1);
        }

        BarSeries series = new BaseBarSeriesBuilder().withName("features").build();
        for (Candle candle : candles) {
            ZonedDateTime endTime = ZonedDateTime.of(candle.getTimestamp(), ZoneId.systemDefault());
            series.addBar(new BaseBar(barDuration, endTime,
                    candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume()));
        }
        return series;
    }
}
