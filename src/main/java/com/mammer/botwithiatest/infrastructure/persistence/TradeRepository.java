package com.mammer.botwithiatest.infrastructure.persistence;

import com.mammer.botwithiatest.domaine.model.TradeStatus;
import com.mammer.botwithiatest.infrastructure.persistence.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatusOrderByOpenedAtDesc(TradeStatus status);
}
