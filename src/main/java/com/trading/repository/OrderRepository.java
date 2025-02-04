package com.trading.repository;

import com.trading.modal.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByUserId(Long userId);
}
