package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.exception.DomainException;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.mt5.MT5BridgeClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradingEngineTest {

    @Mock
    private MT5BridgeClient bridgeClient;
    @Mock
    private RiskManagementService riskManagementService;

    @InjectMocks
    private TradingEngine tradingEngine;

    @Test
    void sendsOrderWhenConnectedAndRiskAccepted() {
        when(bridgeClient.isConnected()).thenReturn(true);
        when(riskManagementService.computePositionSize(1000, 50)).thenReturn(0.2);

        tradingEngine.executeTrade(TradeSignal.BUY, 1000, 50, 1.23, 1.5);

        verify(bridgeClient).sendOrder("BUY", 0.2, 1.23, 1.5);
    }

    @Test
    void rejectsTradeWhenBridgeDisconnected() {
        when(bridgeClient.isConnected()).thenReturn(false);

        assertThatThrownBy(() -> tradingEngine.executeTrade(TradeSignal.BUY, 1000, 50, 1.23, 1.5))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("disconnected");

        verify(bridgeClient, never()).sendOrder(org.mockito.ArgumentMatchers.anyString(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void rejectsTradeWhenRiskLimitExceeded() {
        when(bridgeClient.isConnected()).thenReturn(true);
        when(riskManagementService.computePositionSize(1000, 5)).thenThrow(new DomainException("risk"));

        assertThatThrownBy(() -> tradingEngine.executeTrade(TradeSignal.SELL, 1000, 5, 1.1, 0.9))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("risk");

        verify(bridgeClient, never()).sendOrder(org.mockito.ArgumentMatchers.anyString(), anyDouble(), anyDouble(), anyDouble());
    }
}
