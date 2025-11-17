package com.mammer.botwithiatest.application.strategy;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.MarketTrend;
import com.mammer.botwithiatest.domaine.model.PredictionResult;
import com.mammer.botwithiatest.domaine.model.SwingPoint;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.domaine.model.LiquiditySweep;
import com.mammer.botwithiatest.application.service.SignalService;
import com.mammer.botwithiatest.application.service.MLIntegrationService;

import java.util.Map;
import java.util.List;
import java.util.Optional;

public class StrategyEngine {

    private final SignalService signalService;
    private final LiquiditySweepDetector liquiditySweepDetector;
    private final MLIntegrationService mlIntegrationService;

    public StrategyEngine(SignalService signalService,
                          LiquiditySweepDetector liquiditySweepDetector,
                          MLIntegrationService mlIntegrationService) {
        this.signalService = signalService;
        this.liquiditySweepDetector = liquiditySweepDetector;
        this.mlIntegrationService = mlIntegrationService;
    }

    /**
     * Merge technical indicator signals, liquidity sweeps and ML predictions into one TradeSignal.
     */
    public TradeSignal generateFinalSignal(List<Candle> candles, MarketTrend trend, Map<String, Double> features) {
        TradeSignal indicatorSignal = signalService.generateSignal(candles);
        Optional<LiquiditySweep> sweep = liquiditySweepDetector.detect(candles, trend);
        PredictionResult prediction = mlIntegrationService.predict(features);

        int buyVotes = 0;
        int sellVotes = 0;

        if (indicatorSignal == TradeSignal.BUY) buyVotes++;
        if (indicatorSignal == TradeSignal.SELL) sellVotes++;

        if (sweep.isPresent()) {
            if (sweep.get().getType() == LiquiditySweep.Type.BUY_SWEEP) {
                buyVotes++;
            } else if (sweep.get().getType() == LiquiditySweep.Type.SELL_SWEEP) {
                sellVotes++;
            }
        }

        if (prediction.getSignal() == TradeSignal.BUY) buyVotes++;
        if (prediction.getSignal() == TradeSignal.SELL) sellVotes++;

        if (buyVotes > sellVotes) {
            return TradeSignal.BUY;
        }
        if (sellVotes > buyVotes) {
            return TradeSignal.SELL;
        }
        return TradeSignal.NONE;
    }

}
