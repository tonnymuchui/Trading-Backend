package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.ForgotPassword;
import com.trading.modal.User;

public interface ForgotPasswordService {
    ForgotPassword createForgotPassword(User user, String id, String otp, VerificationType verificationCode, String sendTo);
    ForgotPassword findById(String id);
    ForgotPassword findByUser(Long userId);
    void deleteForgotPassword(ForgotPassword forgotPassword);
}
