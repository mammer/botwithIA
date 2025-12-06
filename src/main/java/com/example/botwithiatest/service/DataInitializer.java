package com.example.botwithiatest.service;

import com.example.botwithiatest.model.Order;
import com.example.botwithiatest.model.OrderSide;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final TradingService tradingService;

    public DataInitializer(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostConstruct
    public void seed() {
        // create two example trades and close one to illustrate metrics
        Order order1 = tradingService.createOrder("EURUSD", OrderSide.BUY,
                new BigDecimal("10000"), new BigDecimal("1.0850"), new BigDecimal("1.0800"), new BigDecimal("1.0950"));
        var trade1 = tradingService.openTradeFromOrder(order1);
        tradingService.closeTrade(trade1.getId(), new BigDecimal("1.0920"));

        Order order2 = tradingService.createOrder("BTCUSD", OrderSide.SELL,
                new BigDecimal("0.50"), new BigDecimal("42000"), new BigDecimal("43000"), new BigDecimal("40000"));
        tradingService.openTradeFromOrder(order2);
    }
}
