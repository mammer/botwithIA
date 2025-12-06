package com.mammer.botwithiatest.infrastructure.persistence;

import com.mammer.botwithiatest.infrastructure.persistence.entity.TradeLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeLogEntryRepository extends JpaRepository<TradeLogEntry, Long> {
}
