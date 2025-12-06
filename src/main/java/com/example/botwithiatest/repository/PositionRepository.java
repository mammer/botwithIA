package com.example.botwithiatest.repository;

import com.example.botwithiatest.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
