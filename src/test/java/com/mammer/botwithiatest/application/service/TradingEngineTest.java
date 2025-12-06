package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradingEngineTest {

    @Mock
    private MT5BridgeClient bridgeClient;
    @Mock
    private RiskManagementService riskManagementService;

    private TradingEngine tradingEngine;

    @BeforeEach
    void setUp() {
        tradingEngine = new TradingEngine(bridgeClient, riskManagementService);
    }

    @Test
    void rejectsTradesWhenDisconnected() {
        when(riskManagementService.computePositionSize(anyDouble(), anyDouble())).thenReturn(1.0);
        when(riskManagementService.isWithinRiskLimits(anyDouble(), anyDouble(), anyDouble())).thenReturn(true);
        when(bridgeClient.isConnected()).thenReturn(false);

        boolean executed = tradingEngine.executeTrade(TradeSignal.BUY, 10_000, 50, 1.0, 2.0);

        assertFalse(executed);
        verify(bridgeClient, never()).sendOrder(anyString(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void rejectsTradesWhenRiskLimitsExceeded() {
        when(riskManagementService.computePositionSize(anyDouble(), anyDouble())).thenReturn(3.0);
        when(riskManagementService.isWithinRiskLimits(anyDouble(), anyDouble(), anyDouble())).thenReturn(false);
        when(bridgeClient.isConnected()).thenReturn(true);

        boolean executed = tradingEngine.executeTrade(TradeSignal.SELL, 5_000, 100, 1.0, 2.0);

        assertFalse(executed);
        verify(bridgeClient, never()).sendOrder(anyString(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void sendsOrdersWhenInputsValidAndConnected() {
        when(riskManagementService.computePositionSize(anyDouble(), anyDouble())).thenReturn(2.5);
        when(riskManagementService.isWithinRiskLimits(anyDouble(), anyDouble(), anyDouble())).thenReturn(true);
        when(bridgeClient.isConnected()).thenReturn(true);
        when(bridgeClient.sendOrder(anyString(), anyDouble(), anyDouble(), anyDouble())).thenReturn(true);

        boolean executed = tradingEngine.executeTrade(TradeSignal.SELL, 8_000, 80, 1.2, 2.4);

        assertTrue(executed);
        ArgumentCaptor<Double> lotCaptor = ArgumentCaptor.forClass(Double.class);
        verify(bridgeClient).sendOrder(org.mockito.Mockito.eq("SELL"), lotCaptor.capture(), org.mockito.Mockito.eq(1.2), org.mockito.Mockito.eq(2.4));
        assertTrue(lotCaptor.getValue() > 0);
    }

    @Test
    void validatesRiskNumbers() {
        assertThrows(IllegalArgumentException.class, () -> tradingEngine.executeTrade(TradeSignal.BUY, -1, 10, 1.0, 2.0));
        assertThrows(IllegalArgumentException.class, () -> tradingEngine.executeTrade(TradeSignal.BUY, 1_000, -5, 1.0, 2.0));
    }
}
