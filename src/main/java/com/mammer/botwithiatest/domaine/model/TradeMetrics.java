package com.mammer.botwithiatest.domaine.model;

public class TradeMetrics {
    private final long totalTrades;
    private final long winningTrades;
    private final double winRate;
    private final double averageR;
    private final double cumulativePnl;

    public TradeMetrics(long totalTrades, long winningTrades, double winRate, double averageR, double cumulativePnl) {
        this.totalTrades = totalTrades;
        this.winningTrades = winningTrades;
        this.winRate = winRate;
        this.averageR = averageR;
        this.cumulativePnl = cumulativePnl;
    }

    public long getTotalTrades() {
        return totalTrades;
    }

    public long getWinningTrades() {
        return winningTrades;
    }

    public double getWinRate() {
        return winRate;
    }

    public double getAverageR() {
        return averageR;
    }

    public double getCumulativePnl() {
        return cumulativePnl;
    }
}
