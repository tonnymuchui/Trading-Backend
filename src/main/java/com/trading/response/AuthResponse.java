package com.trading.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private boolean status;
    private String message;
    private boolean isTwoFactorAuthenticated;
    private String Session;
}
