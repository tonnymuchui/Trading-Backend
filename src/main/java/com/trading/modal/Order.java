package com.trading.modal;

import com.trading.domain.OrderStatus;
import com.trading.domain.OrderType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private User user;
    @Column(nullable = false)
    private OrderType orderType;
    @Column(nullable = false)
    private BigDecimal price;
    private LocalDateTime localDateTime = LocalDateTime.now();
    @Column(nullable = false)
    private OrderStatus orderStatus;
    @ManyToOne
    private OrderItem orderItem;
}
