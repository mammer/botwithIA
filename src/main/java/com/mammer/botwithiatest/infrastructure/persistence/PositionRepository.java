package com.mammer.botwithiatest.infrastructure.persistence;

import com.mammer.botwithiatest.infrastructure.persistence.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
