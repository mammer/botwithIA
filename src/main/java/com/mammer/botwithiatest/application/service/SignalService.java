package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignalService {
    public TradeSignal generateSignal(List<Candle> candles) {
        // we'll fill this later with actual strategy logic
        return TradeSignal.NONE;
    }
}
