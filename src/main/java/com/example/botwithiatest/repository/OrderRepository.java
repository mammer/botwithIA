package com.example.botwithiatest.repository;

import com.example.botwithiatest.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
