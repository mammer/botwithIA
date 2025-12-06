package com.example.botwithiatest.repository;

import com.example.botwithiatest.model.Trade;
import com.example.botwithiatest.model.TradeStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatus(TradeStatus status);
}
