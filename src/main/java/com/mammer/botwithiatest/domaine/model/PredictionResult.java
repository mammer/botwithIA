package com.mammer.botwithiatest.domaine.model;

public class PredictionResult {
    private final TradeSignal signal;
    private final double confidence;

    public PredictionResult(TradeSignal signal, double confidence) {
        this.signal = signal;
        this.confidence = confidence;
    }

    public TradeSignal getSignal() {
        return signal;
    }

    public double getConfidence() {
        return confidence;
    }
}
