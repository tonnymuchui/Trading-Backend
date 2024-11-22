package com.trading.modal;

import com.trading.domain.WalletTransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Wallet wallet;
    private WalletTransactionType type;
    private LocalDateTime timestamp;
    private String TransferId;
    private String purpose;
    private Long amount;
}
