package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.OrderSide;
import com.mammer.botwithiatest.domaine.model.TradeMetrics;
import com.mammer.botwithiatest.domaine.model.TradeStatus;
import com.mammer.botwithiatest.infrastructure.persistence.OrderRepository;
import com.mammer.botwithiatest.infrastructure.persistence.TradeRepository;
import com.mammer.botwithiatest.infrastructure.persistence.entity.Order;
import com.mammer.botwithiatest.infrastructure.persistence.entity.Trade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final TradeLogService tradeLogService;

    public TradeService(TradeRepository tradeRepository,
                        OrderRepository orderRepository,
                        TradeLogService tradeLogService) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
        this.tradeLogService = tradeLogService;
    }

    @Transactional
    public Trade openTrade(String symbol, OrderSide side, double size, double entryPrice, double stopLoss, double takeProfit) {
        Trade trade = new Trade(symbol, side, size, entryPrice, stopLoss, takeProfit);
        Order order = new Order(trade, side, size, entryPrice, stopLoss, takeProfit);
        trade.addOrder(order);
        Trade savedTrade = tradeRepository.save(trade);
        orderRepository.save(order);
        tradeLogService.logOrderEvent(order, "Order filled");
        tradeLogService.logTradeEvent(savedTrade, "Trade opened");
        return savedTrade;
    }

    @Transactional
    public Trade closeTrade(Long tradeId, double exitPrice) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found: " + tradeId));

        trade.setExitPrice(exitPrice);
        trade.setClosedAt(LocalDateTime.now());
        trade.setStatus(TradeStatus.CLOSED);
        double pnl = computePnl(trade.getSide(), trade.getSize(), trade.getEntryPrice(), exitPrice);
        trade.setPnl(pnl);
        if (trade.getRisk() > 0) {
            trade.setRiskReward(pnl / trade.getRisk());
        }
        Trade saved = tradeRepository.save(trade);
        tradeLogService.logTradeEvent(saved, "Trade closed");
        return saved;
    }

    public List<Trade> getTradesByStatus(TradeStatus status) {
        return tradeRepository.findByStatusOrderByOpenedAtDesc(status);
    }

    public TradeMetrics computeMetrics() {
        List<Trade> closedTrades = tradeRepository.findByStatusOrderByOpenedAtDesc(TradeStatus.CLOSED);
        long total = closedTrades.size();
        long winning = closedTrades.stream().filter(t -> t.getPnl() > 0).count();
        double winRate = total == 0 ? 0 : (double) winning / total;
        double averageR = closedTrades.stream()
                .filter(t -> t.getRisk() > 0)
                .mapToDouble(t -> t.getPnl() / t.getRisk())
                .average()
                .orElse(0);
        double cumulativePnl = closedTrades.stream().mapToDouble(Trade::getPnl).sum();
        return new TradeMetrics(total, winning, winRate, averageR, cumulativePnl);
    }

    private double computePnl(OrderSide side, double size, double entryPrice, double exitPrice) {
        double delta = exitPrice - entryPrice;
        return side == OrderSide.BUY ? delta * size : -delta * size;
    }
}
