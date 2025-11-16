package com.mammer.botwithiatest.application.usecase;

import com.mammer.botwithiatest.application.service.SignalService;
import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenerateSignalUseCase {

    private final SignalService signalService;

    public GenerateSignalUseCase(SignalService signalService) {
        this.signalService = signalService;
    }

    public TradeSignal execute(List<Candle> candles) {
        return signalService.generateSignal(candles);
    }
}
