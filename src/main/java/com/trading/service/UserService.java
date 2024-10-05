package com.trading.service;

import com.trading.domain.VerificationType;
import com.trading.modal.User;

public interface UserService {
    public User findUserByEmail(String email);
    public User findUserById(Long id);
    public User findUserByJwtToken(String jwtToken);
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo,User user);
    public User updatePassword(User user, String newPassword);
}
