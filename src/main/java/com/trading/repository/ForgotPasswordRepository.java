package com.trading.repository;

import com.trading.modal.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, String> {
    ForgotPassword findByUserId(Long userId);
}
