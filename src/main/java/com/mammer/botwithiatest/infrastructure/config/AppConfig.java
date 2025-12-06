package com.mammer.botwithiatest.infrastructure.config;

import com.mammer.botwithiatest.domaine.model.SymbolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public SymbolConfig xauUsdSymbolConfig() {
        // Default configuration for trading the XAU/USD pair.
        return new SymbolConfig("XAUUSD", 0.1, 0.05, 0.5, 0.01);
    }
}
