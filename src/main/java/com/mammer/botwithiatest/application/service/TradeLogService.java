package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.TradeEventType;
import com.mammer.botwithiatest.infrastructure.persistence.TradeLogEntryRepository;
import com.mammer.botwithiatest.infrastructure.persistence.entity.Order;
import com.mammer.botwithiatest.infrastructure.persistence.entity.Trade;
import com.mammer.botwithiatest.infrastructure.persistence.entity.TradeLogEntry;
import org.springframework.stereotype.Service;

@Service
public class TradeLogService {

    private final TradeLogEntryRepository tradeLogEntryRepository;

    public TradeLogService(TradeLogEntryRepository tradeLogEntryRepository) {
        this.tradeLogEntryRepository = tradeLogEntryRepository;
    }

    public TradeLogEntry logOrderEvent(Order order, String message) {
        TradeLogEntry log = new TradeLogEntry(
                TradeEventType.ORDER,
                order.getTrade() != null ? order.getTrade().getId() : null,
                order.getId(),
                order.getTrade() != null ? order.getTrade().getSymbol() : null,
                order.getSide(),
                order.getSize(),
                order.getStopLoss(),
                order.getTakeProfit(),
                order.getFillPrice(),
                0,
                message
        );
        return tradeLogEntryRepository.save(log);
    }

    public TradeLogEntry logTradeEvent(Trade trade, String message) {
        TradeLogEntry log = new TradeLogEntry(
                TradeEventType.TRADE,
                trade.getId(),
                null,
                trade.getSymbol(),
                trade.getSide(),
                trade.getSize(),
                trade.getStopLoss(),
                trade.getTakeProfit(),
                trade.getExitPrice(),
                trade.getPnl(),
                message
        );
        return tradeLogEntryRepository.save(log);
    }
}
