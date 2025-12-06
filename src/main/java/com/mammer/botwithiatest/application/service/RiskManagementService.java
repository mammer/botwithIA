package com.mammer.botwithiatest.application.service;


import org.springframework.stereotype.Service;

@Service
public class RiskManagementService {
    public double computePositionSize(double equity, double stopLossPips) {
        // 1% I'm gonna think if i nned to change it or make an interface about this margin
        double riskPercent = 0.01;
        double riskMoney = equity * riskPercent;
        return riskMoney / stopLossPips;
    }

    public double computePositionSize(double equity, double stopLossPips, double riskPercent) {
        double riskMoney = equity * riskPercent;
        return riskMoney / stopLossPips;
    }
}
