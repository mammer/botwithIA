package com.mammer.botwithiatest.domaine.model;

/**
 * Symbol specific configuration used to validate and size orders.
 */
public class SymbolConfig {
    private final String symbol;
    private final double lotSize;
    private final double slippage;
    private final double maxSpread;
    private final double riskPerTrade;

    public SymbolConfig(String symbol, double lotSize, double slippage, double maxSpread, double riskPerTrade) {
        this.symbol = symbol;
        this.lotSize = lotSize;
        this.slippage = slippage;
        this.maxSpread = maxSpread;
        this.riskPerTrade = riskPerTrade;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getLotSize() {
        return lotSize;
    }

    public double getSlippage() {
        return slippage;
    }

    public double getMaxSpread() {
        return maxSpread;
    }

    public double getRiskPerTrade() {
        return riskPerTrade;
    }
}
