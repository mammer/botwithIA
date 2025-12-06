package com.mammer.botwithiatest.application.strategy;

import com.mammer.botwithiatest.application.service.MLIntegrationService;
import com.mammer.botwithiatest.application.service.SignalService;
import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.LiquiditySweep;
import com.mammer.botwithiatest.domaine.model.MarketTrend;
import com.mammer.botwithiatest.domaine.model.PredictionResult;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrategyEngineTest {

    @Mock
    private SignalService signalService;
    @Mock
    private LiquiditySweepDetector liquiditySweepDetector;
    @Mock
    private MLIntegrationService mlIntegrationService;

    private StrategyEngine strategyEngine;
    private List<Candle> candles;
    private Map<String, Double> features;

    @BeforeEach
    void setUp() {
        strategyEngine = new StrategyEngine(signalService, liquiditySweepDetector, mlIntegrationService);
        candles = List.of(new Candle(ZonedDateTime.now(), 1, 2, 0.5, 1.5, 100));
        features = Map.of("atr", 1.0);
    }

    @Test
    void returnsBuyWhenMajorityVotesBuy() {
        when(signalService.generateSignal(any())).thenReturn(TradeSignal.BUY);
        when(liquiditySweepDetector.detect(any(), any())).thenReturn(Optional.of(new LiquiditySweep(LiquiditySweep.Type.BUY_SWEEP, 1.0, null, null)));
        when(mlIntegrationService.predict(features)).thenReturn(new PredictionResult(TradeSignal.BUY, 0.7));

        TradeSignal result = strategyEngine.generateFinalSignal(candles, MarketTrend.UP, features);

        assertThat(result).isEqualTo(TradeSignal.BUY);
    }

    @Test
    void returnsSellWhenSellVotesDominate() {
        when(signalService.generateSignal(any())).thenReturn(TradeSignal.SELL);
        when(liquiditySweepDetector.detect(any(), any())).thenReturn(Optional.of(new LiquiditySweep(LiquiditySweep.Type.SELL_SWEEP, 1.0, null, null)));
        when(mlIntegrationService.predict(features)).thenReturn(new PredictionResult(TradeSignal.NONE, 0.51));

        TradeSignal result = strategyEngine.generateFinalSignal(candles, MarketTrend.DOWN, features);

        assertThat(result).isEqualTo(TradeSignal.SELL);
    }

    @Test
    void returnsNoneOnTie() {
        when(signalService.generateSignal(any())).thenReturn(TradeSignal.BUY);
        when(liquiditySweepDetector.detect(any(), any())).thenReturn(Optional.of(new LiquiditySweep(LiquiditySweep.Type.SELL_SWEEP, 1.0, null, null)));
        when(mlIntegrationService.predict(features)).thenReturn(new PredictionResult(TradeSignal.NONE, 0.5));

        TradeSignal result = strategyEngine.generateFinalSignal(candles, MarketTrend.RANGE, features);

        assertThat(result).isEqualTo(TradeSignal.NONE);
    }
}
