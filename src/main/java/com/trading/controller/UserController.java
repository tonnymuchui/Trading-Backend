package com.trading.controller;

import com.trading.domain.VerificationType;
import com.trading.modal.ForgotPassword;
import com.trading.modal.User;
import com.trading.modal.VerificationCode;
import com.trading.request.ForgotPasswordRequest;
import com.trading.request.ResetPasswordRequest;
import com.trading.response.ApiResponse;
import com.trading.response.AuthResponse;
import com.trading.service.EmailService;
import com.trading.service.ForgotPasswordService;
import com.trading.service.UserService;
import com.trading.service.VerificationCodeService;
import com.trading.utils.OtpUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final ForgotPasswordService forgotPasswordService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token) {
        log.info("Fetching user profile for token: {}", token);
        User user = userService.findUserByJwtToken(token);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String token,
            @PathVariable VerificationType verificationType) throws MessagingException {
        log.info("Sending verification OTP for token: {}, verificationType: {}", token, verificationType);
        User user = userService.findUserByJwtToken(token);

        // Fetch or create new verification code
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
            log.info("Generated new verification code: {}", verificationCode.getOtp());
        }

        // Send email if verification type is EMAIL
        if (verificationType == VerificationType.EMAIL) {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode.getOtp());
            log.info("Verification email sent to: {}", user.getEmail());
        }

        return ResponseEntity.ok("Verification OTP sent successfully.");
    }

    @PatchMapping("/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
            @RequestHeader("Authorization") String token, @PathVariable String otp) {
        log.info("Enabling two-factor authentication for token: {}, OTP: {}", token, otp);
        User user = userService.findUserByJwtToken(token);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            log.warn("Invalid OTP provided for user ID: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OTP");
        }

        String contact = verificationCode.getVerificationType() == VerificationType.EMAIL
                ? verificationCode.getEmail()
                : verificationCode.getMobileNumber();

        User updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), contact, user);
        verificationCodeService.deleteVerificationCodeById(verificationCode);
        log.info("Two-factor authentication enabled for user ID: {}", user.getId());

        return ResponseEntity.ok(updatedUser);
    }


    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest) throws MessagingException {
        log.info("Sending forgot  OTP for token: {}, verificationType: {}", forgotPasswordRequest.getSendTo(), forgotPasswordRequest);
        User user = userService.findUserByEmail(forgotPasswordRequest.getSendTo());
        String otp = OtpUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        ForgotPassword forgotPassword = forgotPasswordService.findByUser(user.getId());
        if (forgotPassword == null) {
            forgotPassword = forgotPasswordService.createForgotPassword(user,id,otp,forgotPasswordRequest.getVerificationType(), forgotPasswordRequest.getSendTo());
        }
        if (forgotPasswordRequest.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationEmail(user.getEmail(), forgotPassword.getOtp());
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setSession(forgotPassword.getId());
        authResponse.setMessage("OTP Rest sent successfully.");
        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPasswordOtp(
            @RequestHeader("Authorization") String token,
            @RequestParam ResetPasswordRequest resetPasswordRequest,
            @RequestParam String id) throws MessagingException {
        log.info("Enabling reset password for token: {}, OTP: {}",resetPasswordRequest, id);
        ForgotPassword forgotPassword =forgotPasswordService.findById(id);

        boolean isVerified = forgotPassword.getOtp().equals(resetPasswordRequest.getOtp());
        if (isVerified) {
            userService.updatePassword(forgotPassword.getUser(),resetPasswordRequest.getPassword());
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setMessage("Password updated successfully.");
            return ResponseEntity.ok(apiResponse);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OTP");
    }
}
