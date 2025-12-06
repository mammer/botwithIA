package com.mammer.botwithiatest.infrastructure.persistence.entity;

import com.mammer.botwithiatest.domaine.model.TradeEventType;
import com.mammer.botwithiatest.domaine.model.OrderSide;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "trade_logs")
public class TradeLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TradeEventType eventType;

    private Long tradeId;
    private Long orderId;
    private String symbol;

    @Enumerated(EnumType.STRING)
    private OrderSide side;

    private double size;
    private double stopLoss;
    private double takeProfit;
    private double fillPrice;
    private double pnl;
    private String message;
    private LocalDateTime eventTime;

    public TradeLogEntry() {
    }

    public TradeLogEntry(TradeEventType eventType, Long tradeId, Long orderId, String symbol, OrderSide side, double size,
                          double stopLoss, double takeProfit, double fillPrice, double pnl, String message) {
        this.eventType = eventType;
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.size = size;
        this.stopLoss = stopLoss;
        this.takeProfit = takeProfit;
        this.fillPrice = fillPrice;
        this.pnl = pnl;
        this.message = message;
        this.eventTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public TradeEventType getEventType() {
        return eventType;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public double getSize() {
        return size;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public double getTakeProfit() {
        return takeProfit;
    }

    public double getFillPrice() {
        return fillPrice;
    }

    public double getPnl() {
        return pnl;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }
}
