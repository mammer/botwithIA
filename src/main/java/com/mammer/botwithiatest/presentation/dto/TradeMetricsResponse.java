package com.mammer.botwithiatest.presentation.dto;

public class TradeMetricsResponse {
    private long totalTrades;
    private long winningTrades;
    private double winRate;
    private double averageR;
    private double cumulativePnl;

    public long getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(long totalTrades) {
        this.totalTrades = totalTrades;
    }

    public long getWinningTrades() {
        return winningTrades;
    }

    public void setWinningTrades(long winningTrades) {
        this.winningTrades = winningTrades;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public double getAverageR() {
        return averageR;
    }

    public void setAverageR(double averageR) {
        this.averageR = averageR;
    }

    public double getCumulativePnl() {
        return cumulativePnl;
    }

    public void setCumulativePnl(double cumulativePnl) {
        this.cumulativePnl = cumulativePnl;
    }
}
