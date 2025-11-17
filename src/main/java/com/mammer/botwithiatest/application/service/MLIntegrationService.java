package com.mammer.botwithiatest.application.service;

import com.mammer.botwithiatest.domaine.model.PredictionResult;
import com.mammer.botwithiatest.domaine.model.TradeSignal;
import com.mammer.botwithiatest.infrastructure.ml.MLHttpClient;
import com.mammer.botwithiatest.infrastructure.ml.MLPredictionResponse;
import com.mammer.botwithiatest.presentation.dto.PredictionRequestDTO;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class MLIntegrationService {

    private final MLHttpClient mlHttpClient;

    public MLIntegrationService(MLHttpClient mlHttpClient) {
        this.mlHttpClient = mlHttpClient;
    }

    public PredictionResult predict(Map<String, Double> features) {
        PredictionRequestDTO request = new PredictionRequestDTO();
        request.setFeatures(features);

        MLPredictionResponse response = mlHttpClient.predict(request);
        TradeSignal signal = mapToTradeSignal(response.label(), response.confidence());

        return new PredictionResult(signal, response.confidence());
    }

    private TradeSignal mapToTradeSignal(String label, double confidence) {
        if (label != null) {
            return switch (label.toUpperCase(Locale.ROOT)) {
                case "BUY" -> TradeSignal.BUY;
                case "SELL" -> TradeSignal.SELL;
                default -> TradeSignal.NONE;
            };
        }

        if (confidence > 0.55) {
            return TradeSignal.BUY;
        }
        if (confidence < 0.45) {
            return TradeSignal.SELL;
        }
        return TradeSignal.NONE;
    }
}
