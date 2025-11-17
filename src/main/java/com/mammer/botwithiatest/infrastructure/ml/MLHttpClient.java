package com.mammer.botwithiatest.infrastructure.ml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mammer.botwithiatest.presentation.dto.PredictionRequestDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MLHttpClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String predictionUrl;

    public MLHttpClient(@Value("${ml.endpoint:http://localhost:8000/predict}") String predictionUrl) {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.predictionUrl = predictionUrl;
    }

    public MLPredictionResponse predict(PredictionRequestDTO request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            RequestBody body = RequestBody.create(payload, JSON);

            Request httpRequest = new Request.Builder()
                    .url(predictionUrl)
                    .post(body)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response.code());
                }
                if (response.body() == null) {
                    throw new IOException("Empty response body from ML service");
                }

                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                String label = extractLabel(jsonNode);
                double confidence = extractConfidence(jsonNode);
                return new MLPredictionResponse(label, confidence);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to call ML prediction service", e);
        }
    }

    private String extractLabel(JsonNode jsonNode) {
        if (jsonNode.hasNonNull("prediction")) {
            return jsonNode.get("prediction").asText();
        }
        if (jsonNode.hasNonNull("class")) {
            return jsonNode.get("class").asText();
        }
        return null;
    }

    private double extractConfidence(JsonNode jsonNode) {
        if (jsonNode.hasNonNull("confidence")) {
            return jsonNode.get("confidence").asDouble();
        }
        if (jsonNode.hasNonNull("probability")) {
            return jsonNode.get("probability").asDouble();
        }
        return 0.5;
    }
}
