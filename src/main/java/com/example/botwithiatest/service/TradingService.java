package com.example.botwithiatest.service;

import com.example.botwithiatest.model.Order;
import com.example.botwithiatest.model.OrderStatus;
import com.example.botwithiatest.model.OrderSide;
import com.example.botwithiatest.model.Trade;
import com.example.botwithiatest.model.TradeLogEventType;
import com.example.botwithiatest.model.TradeStatus;
import com.example.botwithiatest.repository.OrderRepository;
import com.example.botwithiatest.repository.TradeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradingService {

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final TradeLogService tradeLogService;

    public TradingService(OrderRepository orderRepository, TradeRepository tradeRepository, TradeLogService tradeLogService) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.tradeLogService = tradeLogService;
    }

    public List<Trade> getOpenTrades() {
        return tradeRepository.findByStatus(TradeStatus.OPEN);
    }

    public List<Trade> getClosedTrades() {
        return tradeRepository.findByStatus(TradeStatus.CLOSED);
    }

    public Map<String, Object> getAggregatedMetrics() {
        List<Trade> closed = getClosedTrades();
        int totalClosed = closed.size();
        long wins = closed.stream().filter(t -> t.getPnl() != null && t.getPnl().compareTo(BigDecimal.ZERO) > 0).count();
        BigDecimal winRate = totalClosed == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(wins * 100.0 / totalClosed).setScale(2, RoundingMode.HALF_UP);

        BigDecimal cumulativePnl = closed.stream()
                .map(t -> t.getPnl() == null ? BigDecimal.ZERO : t.getPnl())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BigDecimal> rMultiples = closed.stream()
                .map(this::calculateRMultiple)
                .filter(r -> r != null)
                .toList();
        BigDecimal averageR = rMultiples.isEmpty() ? BigDecimal.ZERO :
                rMultiples.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(rMultiples.size()), 4, RoundingMode.HALF_UP);

        return Map.of(
                "closedCount", totalClosed,
                "winRate", winRate,
                "averageR", averageR,
                "cumulativePnl", cumulativePnl
        );
    }

    public BigDecimal calculatePnL(Trade trade) {
        if (trade.getExitPrice() == null || trade.getEntryPrice() == null || trade.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal priceDiff = trade.getExitPrice().subtract(trade.getEntryPrice());
        if (OrderSide.SELL.equals(trade.getSide())) {
            priceDiff = trade.getEntryPrice().subtract(trade.getExitPrice());
        }
        return priceDiff.multiply(trade.getQuantity());
    }

    private BigDecimal calculateRMultiple(Trade trade) {
        if (trade.getStopLoss() == null || trade.getEntryPrice() == null || trade.getQuantity() == null || trade.getPnl() == null) {
            return null;
        }
        BigDecimal riskPerUnit = trade.getEntryPrice().subtract(trade.getStopLoss()).abs();
        if (riskPerUnit.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        BigDecimal totalRisk = riskPerUnit.multiply(trade.getQuantity());
        return trade.getPnl().divide(totalRisk, 4, RoundingMode.HALF_UP);
    }

    @Transactional
    public Trade openTradeFromOrder(Order order) {
        order.setStatus(OrderStatus.FILLED);
        order.setFilledPrice(order.getLimitPrice());
        order.setFilledAt(LocalDateTime.now());
        orderRepository.save(order);
        tradeLogService.logOrderEvent(order, TradeLogEventType.ORDER_FILLED, "Order filled and opening trade");

        Trade trade = new Trade();
        trade.setSymbol(order.getSymbol());
        trade.setSide(order.getSide());
        trade.setQuantity(order.getQuantity());
        trade.setEntryPrice(order.getFilledPrice());
        trade.setStopLoss(order.getStopLoss());
        trade.setTakeProfit(order.getTakeProfit());
        trade.setStatus(TradeStatus.OPEN);
        tradeRepository.save(trade);
        tradeLogService.logTradeEvent(trade, TradeLogEventType.TRADE_OPENED, "Trade opened from order");
        return trade;
    }

    @Transactional
    public Trade closeTrade(Long tradeId, BigDecimal exitPrice) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();
        trade.setExitPrice(exitPrice);
        trade.setClosedAt(LocalDateTime.now());
        trade.setStatus(TradeStatus.CLOSED);
        trade.setPnl(calculatePnL(trade));
        tradeRepository.save(trade);
        tradeLogService.logTradeEvent(trade, TradeLogEventType.TRADE_CLOSED, "Trade closed");
        return trade;
    }

    @Transactional
    public Order createOrder(String symbol, OrderSide side, BigDecimal quantity, BigDecimal limitPrice, BigDecimal stopLoss, BigDecimal takeProfit) {
        Order order = new Order();
        order.setSymbol(symbol);
        order.setSide(side);
        order.setQuantity(quantity);
        order.setLimitPrice(limitPrice);
        order.setStopLoss(stopLoss);
        order.setTakeProfit(takeProfit);
        order.setStatus(OrderStatus.NEW);
        Order saved = orderRepository.save(order);
        tradeLogService.logOrderEvent(saved, TradeLogEventType.ORDER_CREATED, "Order created");
        return saved;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll().stream()
                .sorted((a, b) -> a.getOpenedAt().compareTo(b.getOpenedAt()))
                .collect(Collectors.toList());
    }
}
