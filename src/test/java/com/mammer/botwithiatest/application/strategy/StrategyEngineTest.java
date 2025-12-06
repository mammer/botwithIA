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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
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

    @BeforeEach
    void setUp() {
        strategyEngine = new StrategyEngine(signalService, liquiditySweepDetector, mlIntegrationService);
        candles = List.of(
                new Candle(ZonedDateTime.now().minusMinutes(5), 1, 2, 0.5, 1.5, 1000),
                new Candle(ZonedDateTime.now(), 1.5, 2.5, 1, 2, 1200)
        );
    }

    @Test
    void returnsBuyWhenMajorityVotesAreBullish() {
        when(signalService.generateSignal(anyList())).thenReturn(TradeSignal.BUY);
        when(liquiditySweepDetector.detect(anyList(), any())).thenReturn(Optional.of(
                new LiquiditySweep(LiquiditySweep.Type.BUY_SWEEP, 1.8, ZonedDateTime.now().minusMinutes(1).toLocalDateTime(), ZonedDateTime.now().toLocalDateTime())
        ));
        when(mlIntegrationService.predict(anyMap())).thenReturn(new PredictionResult(TradeSignal.SELL, 0.6));

        TradeSignal signal = strategyEngine.generateFinalSignal(candles, MarketTrend.UP, Map.of("feature", 1.0));

        assertEquals(TradeSignal.BUY, signal);
    }

    @Test
    void returnsNoneOnTie() {
        when(signalService.generateSignal(anyList())).thenReturn(TradeSignal.BUY);
        when(liquiditySweepDetector.detect(anyList(), any())).thenReturn(Optional.empty());
        when(mlIntegrationService.predict(anyMap())).thenReturn(new PredictionResult(TradeSignal.SELL, 0.9));

        TradeSignal signal = strategyEngine.generateFinalSignal(candles, MarketTrend.RANGE, Map.of());

        assertEquals(TradeSignal.NONE, signal);
    }
}
