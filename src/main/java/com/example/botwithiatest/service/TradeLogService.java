package com.example.botwithiatest.service;

import com.example.botwithiatest.model.Order;
import com.example.botwithiatest.model.Trade;
import com.example.botwithiatest.model.TradeLogEntry;
import com.example.botwithiatest.model.TradeLogEventType;
import com.example.botwithiatest.repository.TradeLogRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeLogService {

    private final TradeLogRepository tradeLogRepository;

    public TradeLogService(TradeLogRepository tradeLogRepository) {
        this.tradeLogRepository = tradeLogRepository;
    }

    @Transactional
    public TradeLogEntry logOrderEvent(Order order, TradeLogEventType eventType, String description) {
        TradeLogEntry entry = new TradeLogEntry();
        entry.setEventType(eventType);
        entry.setSymbol(order.getSymbol());
        entry.setSide(order.getSide());
        entry.setQuantity(order.getQuantity());
        entry.setStopLoss(order.getStopLoss());
        entry.setTakeProfit(order.getTakeProfit());
        entry.setFillPrice(order.getFilledPrice());
        entry.setDescription(description);
        return tradeLogRepository.save(entry);
    }

    @Transactional
    public TradeLogEntry logTradeEvent(Trade trade, TradeLogEventType eventType, String description) {
        TradeLogEntry entry = new TradeLogEntry();
        entry.setEventType(eventType);
        entry.setSymbol(trade.getSymbol());
        entry.setSide(trade.getSide());
        entry.setQuantity(trade.getQuantity());
        entry.setStopLoss(trade.getStopLoss());
        entry.setTakeProfit(trade.getTakeProfit());
        entry.setFillPrice(trade.getExitPrice() != null ? trade.getExitPrice() : trade.getEntryPrice());
        entry.setPnl(trade.getPnl());
        entry.setDescription(description);
        return tradeLogRepository.save(entry);
    }

    public BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
