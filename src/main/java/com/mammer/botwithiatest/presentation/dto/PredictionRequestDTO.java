package com.mammer.botwithiatest.presentation.dto;

import java.util.Map;

public class PredictionRequestDTO {
    private Map<String, Double> features;

    public PredictionRequestDTO() {
    }

    public PredictionRequestDTO(Map<String, Double> features) {
        this.features = features;
    }

    public Map<String, Double> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Double> features) {
        this.features = features;
    }
}
