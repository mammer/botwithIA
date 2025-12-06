package com.mammer.botwithiatest.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RiskManagementService {

    private final double riskPercent;
    private final double maxRiskPercent;
    private final double minStopLossPips;

    public RiskManagementService(
            @Value("${trading.risk.percent:0.01}") double riskPercent,
            @Value("${trading.risk.max-percent:0.02}") double maxRiskPercent,
            @Value("${trading.risk.min-stop-loss-pips:1}") double minStopLossPips) {
        this.riskPercent = riskPercent;
        this.maxRiskPercent = maxRiskPercent;
        this.minStopLossPips = minStopLossPips;
    }

    public double computePositionSize(double equity, double stopLossPips) {
        validatePositive(equity, "equity");
        validateStopLoss(stopLossPips);

        double riskMoney = equity * riskPercent;
        return riskMoney / stopLossPips;
    }

    public boolean isWithinRiskLimits(double equity, double stopLossPips, double lotSize) {
        validatePositive(equity, "equity");
        validateStopLoss(stopLossPips);
        validatePositive(lotSize, "lotSize");

        double riskPerTrade = (stopLossPips * lotSize) / equity;
        return riskPerTrade <= maxRiskPercent;
    }

    private void validateStopLoss(double stopLossPips) {
        if (stopLossPips < minStopLossPips) {
            throw new IllegalArgumentException("stopLossPips must be at least " + minStopLossPips);
        }
    }

    private void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
    }
}
