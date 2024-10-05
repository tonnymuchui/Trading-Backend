package com.trading.modal;

import com.trading.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @OneToOne
    private User user;
    private String otp;
    private VerificationType verificationType;
    private String sendTo;
}
