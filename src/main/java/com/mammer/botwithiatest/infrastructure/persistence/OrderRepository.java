package com.mammer.botwithiatest.infrastructure.persistence;

import com.mammer.botwithiatest.infrastructure.persistence.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
