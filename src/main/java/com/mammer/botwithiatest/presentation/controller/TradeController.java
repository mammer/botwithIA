package com.mammer.botwithiatest.presentation.controller;

import com.mammer.botwithiatest.application.service.TradeService;
import com.mammer.botwithiatest.domaine.model.TradeMetrics;
import com.mammer.botwithiatest.domaine.model.TradeStatus;
import com.mammer.botwithiatest.infrastructure.persistence.entity.Trade;
import com.mammer.botwithiatest.presentation.dto.CloseTradeRequest;
import com.mammer.botwithiatest.presentation.dto.OpenTradeRequest;
import com.mammer.botwithiatest.presentation.dto.TradeMetricsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<Trade> openTrade(@RequestBody OpenTradeRequest request) {
        Trade trade = tradeService.openTrade(
                request.getSymbol(),
                request.getSide(),
                request.getSize(),
                request.getEntryPrice(),
                request.getStopLoss(),
                request.getTakeProfit()
        );
        return ResponseEntity.ok(trade);
    }

    @PostMapping("/{tradeId}/close")
    public ResponseEntity<Trade> closeTrade(@PathVariable Long tradeId, @RequestBody CloseTradeRequest request) {
        return ResponseEntity.ok(tradeService.closeTrade(tradeId, request.getExitPrice()));
    }

    @GetMapping("/open")
    public List<Trade> getOpenTrades() {
        return tradeService.getTradesByStatus(TradeStatus.OPEN);
    }

    @GetMapping("/closed")
    public List<Trade> getClosedTrades() {
        return tradeService.getTradesByStatus(TradeStatus.CLOSED);
    }

    @GetMapping("/metrics")
    public TradeMetricsResponse getMetrics() {
        TradeMetrics metrics = tradeService.computeMetrics();
        TradeMetricsResponse response = new TradeMetricsResponse();
        response.setTotalTrades(metrics.getTotalTrades());
        response.setWinningTrades(metrics.getWinningTrades());
        response.setWinRate(metrics.getWinRate());
        response.setAverageR(metrics.getAverageR());
        response.setCumulativePnl(metrics.getCumulativePnl());
        return response;
    }
}
