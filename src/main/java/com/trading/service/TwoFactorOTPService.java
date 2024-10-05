package com.trading.service;

import com.trading.modal.TwoFactorOTP;
import com.trading.modal.User;

public interface TwoFactorOTPService {
    TwoFactorOTP createTwoFactorOTP(User user, String otp, String jwt);
    TwoFactorOTP findByUser(Long userId);
    TwoFactorOTP findById(String Id);
    boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorOTP, String otp);
    void deleteTwoFactorOTP(TwoFactorOTP twoFactorOTP);
}