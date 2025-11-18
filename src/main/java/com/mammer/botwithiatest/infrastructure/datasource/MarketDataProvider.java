package com.mammer.botwithiatest.infrastructure.datasource;

import com.mammer.botwithiatest.domaine.model.Candle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketDataProvider {

    public List<Candle> fetchHistoricalData(String symbol, int lookback) {
        // Placeholder for integration with broker/exchange historical API
        return new ArrayList<>();
    }

    public List<Candle> fetchLatestCandles(String symbol, int limit) {
        // Placeholder returning empty list. Real implementation would hit a live price feed
        return new ArrayList<>();
    }
}
