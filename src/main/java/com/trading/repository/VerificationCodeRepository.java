package com.trading.repository;

import com.trading.modal.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    public VerificationCode findByUserId(Long user_id);

}
