package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.ForgotPassword;
import com.trading.modal.User;
import com.trading.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordServiceImp implements ForgotPasswordService {
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    public ForgotPasswordServiceImp(ForgotPasswordRepository forgotPasswordRepository) {
        this.forgotPasswordRepository = forgotPasswordRepository;
    }
    @Override
    public ForgotPassword createForgotPassword(User user, String id, String otp, VerificationType verificationCode, String sendTo) {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setUser(user);
        forgotPassword.setSendTo(sendTo);
        forgotPassword.setOtp(otp);
        forgotPassword.setId(id);
        return forgotPasswordRepository.save(forgotPassword);
    }

    @Override
    public ForgotPassword findById(String id) {
        Optional<ForgotPassword> forgotPassword = forgotPasswordRepository.findById(id);
        return forgotPassword.orElse(null);
    }

    @Override
    public ForgotPassword findByUser(Long userId) {
        return forgotPasswordRepository.findByUserId(userId);
    }

    @Override
    public void deleteForgotPassword(ForgotPassword forgotPassword) {
        forgotPasswordRepository.delete(forgotPassword);
    }
}
