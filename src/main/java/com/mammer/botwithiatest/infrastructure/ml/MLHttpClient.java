package com.mammer.botwithiatest.infrastructure.ml;

import com.mammer.botwithiatest.presentation.dto.PredictionRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class MLHttpClient {
    public double predict(PredictionRequestDTO request) {
        // later: call Python FastAPI
        return 0.5; // neutral probability
    }
}
