package com.example.botwithiatest.controller;

import com.example.botwithiatest.model.Trade;
import com.example.botwithiatest.service.TradingService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TradeController {

    private final TradingService tradingService;

    public TradeController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @GetMapping("/trades/open")
    public List<Trade> openTrades() {
        return tradingService.getOpenTrades();
    }

    @GetMapping("/trades/closed")
    public List<Trade> closedTrades() {
        return tradingService.getClosedTrades();
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        return tradingService.getAggregatedMetrics();
    }
}
