package com.mammer.botwithiatest.application.usecase;

import com.mammer.botwithiatest.domaine.model.Candle;
import com.mammer.botwithiatest.domaine.repository.ModelStorageRepository;
import com.mammer.botwithiatest.infrastructure.datasource.MarketDataProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainModelUseCase {

    private final MarketDataProvider marketDataProvider;
    private final ModelStorageRepository modelStorageRepository;

    public TrainModelUseCase(MarketDataProvider marketDataProvider, ModelStorageRepository modelStorageRepository) {
        this.marketDataProvider = marketDataProvider;
        this.modelStorageRepository = modelStorageRepository;
    }

    public void train(String symbol, int lookback) {
        List<Candle> candles = marketDataProvider.fetchHistoricalData(symbol, lookback);
        Map<String, Double> featureAverages = extractFeatures(candles);
        modelStorageRepository.saveModel(symbol, featureAverages);
    }

    private Map<String, Double> extractFeatures(List<Candle> candles) {
        Map<String, Double> features = new HashMap<>();
        if (candles == null || candles.isEmpty()) {
            return features;
        }

        double sumClose = 0;
        double sumHigh = 0;
        double sumLow = 0;
        for (Candle candle : candles) {
            sumClose += candle.getClose();
            sumHigh += candle.getHigh();
            sumLow += candle.getLow();
        }

        double count = candles.size();
        features.put("avg_close", sumClose / count);
        features.put("avg_high", sumHigh / count);
        features.put("avg_low", sumLow / count);

        return features;
    }
}
