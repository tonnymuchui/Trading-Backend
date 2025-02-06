package com.trading.request;

import com.trading.domain.VerificationType;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String sendTo;
    private VerificationType verificationType;
}
