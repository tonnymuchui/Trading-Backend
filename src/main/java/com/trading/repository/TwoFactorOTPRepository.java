package com.trading.repository;

import com.trading.modal.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwoFactorOTPRepository extends JpaRepository<TwoFactorOTP, Long> {
    TwoFactorOTP findByUserId(Long userId);
}
