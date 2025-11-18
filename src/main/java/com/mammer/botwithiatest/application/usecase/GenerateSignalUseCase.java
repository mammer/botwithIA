package com.mammer.botwithiatest.application.usecase;

import com.mammer.botwithiatest.application.service.SignalService;
import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateSignalUseCase {

    private  SignalService signalService;


    /**
     * Called by scheduler or controller to generate a trading signal
     * based on recent market data.
     */
    public TradeSignal generate(List<Candle> candles) {

        if (candles == null || candles.size() < 50) {
            return TradeSignal.NONE;
        }
// update
        return signalService.generateSignal(candles);
    }
}
