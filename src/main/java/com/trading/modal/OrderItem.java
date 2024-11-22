package com.trading.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonIgnore
    @OneToOne
    private Order order;
    private double quantity;
    @ManyToOne
    private Coin coin;
    private double buyPrice;
    private double sellPrice;
    }
