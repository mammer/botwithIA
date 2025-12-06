package com.example.botwithiatest.repository;

import com.example.botwithiatest.model.TradeLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeLogRepository extends JpaRepository<TradeLogEntry, Long> {
}
