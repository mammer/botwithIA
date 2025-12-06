package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.exception.DomainException;
import org.springframework.stereotype.Service;

@Service
public class RiskManagementService {

    private static final double RISK_PERCENT = 0.01;
    private static final double MAX_RISK_PERCENT = 0.02;
    private static final double MIN_STOP_LOSS_PIPS = 0.1;

    public double computePositionSize(double equity, double stopLossPips) {
        validateInputs(equity, stopLossPips);

        double riskMoney = equity * RISK_PERCENT;
        double maxRiskMoney = equity * MAX_RISK_PERCENT;

        if (riskMoney > maxRiskMoney) {
            throw new DomainException("Risk percent exceeds configured maximum limit");
        }

        double lotSize = riskMoney / stopLossPips;

        if (Double.isNaN(lotSize) || Double.isInfinite(lotSize) || lotSize <= 0) {
            throw new DomainException("Calculated lot size is invalid");
        }

        return lotSize;
    }

    public void validateInputs(double equity, double stopLossPips) {
        if (equity <= 0) {
            throw new DomainException("Equity must be greater than zero");
        }

        if (stopLossPips < MIN_STOP_LOSS_PIPS) {
            throw new DomainException("Stop loss pips must be greater than " + MIN_STOP_LOSS_PIPS);
        }
    }
}
