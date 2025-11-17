package com.mammer.botwithiatest.application.strategy;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.LiquiditySweep;
import com.mammer.botwithiatest.domaine.model.MarketTrend;
import java.util.List;
import java.util.Optional;

// ===========  respecting the book of Tamas
/**
 * Detects liquidity sweeps (stop hunts) around recent swing highs / lows.
 *
 * Logic (simplified from AlphaFX book):
 *  - Find recent swing highs / swing lows (zigzag-like)
 *  - If current candle takes out a swing high but closes back below it => SELL sweep
 *  - If current candle takes out a swing low but closes back above it => BUY sweep
 */

public class LiquiditySweepDetector {
    /**
     * Main entry point.
     * @param candles ordered list, oldest → newest
     * @param trend   current market trend (UP / DOWN / RANGE)
     */
    public Optional<LiquiditySweep> detect(List<Candle> candles, MarketTrend trend) {
        if (candles == null || candles.size() < 5) {
            return Optional.empty();
        }

        // Last candle (the one we are analyzing now)
        Candle last = candles.get(candles.size() - 1);

        // Find recent swing highs/lows
        int lookback = Math.min(candles.size() - 1, 30); // lookback window
        int startIdx = candles.size() - lookback;

        int lastSwingHighIdx = -1;
        int lastSwingLowIdx = -1;

        for (int i = startIdx + 1; i < candles.size() - 1; i++) {
            Candle prev = candles.get(i - 1);
            Candle c = candles.get(i);
            Candle next = candles.get(i + 1);

            // Swing high: high[i] > high[i-1] and high[i] > high[i+1]
            if (c.getHigh() > prev.getHigh() && c.getHigh() > next.getHigh()) {
                lastSwingHighIdx = i;
            }
            // Swing low: low[i] < low[i-1] and low[i] < low[i+1]
            if (c.getLow() < prev.getLow() && c.getLow() < next.getLow()) {
                lastSwingLowIdx = i;
            }
        }

        LiquiditySweep sweep = null;

        if (trend == MarketTrend.DOWN || trend == MarketTrend.RANGE) {
            // Look for SELL sweep (stop hunt above swing high)
            if (lastSwingHighIdx != -1) {
                Candle swingHigh = candles.get(lastSwingHighIdx);
                double level = swingHigh.getHigh();

                boolean tookOutHigh = last.getHigh() > level;
                boolean closedBackBelow = last.getClose() < level;

                if (tookOutHigh && closedBackBelow) {
                    sweep = new LiquiditySweep(
                            LiquiditySweep.Type.SELL_SWEEP,
                            level,
                            // i should verify localdatetime with
                            swingHigh.getTimestamp(),
                            last.getTimestamp()
                    );
                }
            }
        }

        if (sweep == null && (trend == MarketTrend.UP || trend == MarketTrend.RANGE)) {
            // Look for BUY sweep (stop hunt below swing low)
            if (lastSwingLowIdx != -1) {
                Candle swingLow = candles.get(lastSwingLowIdx);
                double level = swingLow.getLow();

                boolean tookOutLow = last.getLow() < level;
                boolean closedBackAbove = last.getClose() > level;

                if (tookOutLow && closedBackAbove) {
                    sweep = new LiquiditySweep(
                            LiquiditySweep.Type.BUY_SWEEP,
                            level,
                            swingLow.getTimestamp(),
                            last.getTimestamp()
                    );
                }
            }
        }

        return Optional.ofNullable(sweep);
    }
}