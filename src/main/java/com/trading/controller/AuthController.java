package com.trading.controller;

import com.trading.config.JwtProvider;
import com.trading.modal.TwoFactorAuth;
import com.trading.modal.TwoFactorOTP;
import com.trading.modal.User;
import com.trading.repository.UserRepository;
import com.trading.response.AuthResponse;
import com.trading.service.CustomerUserDetailsService;
import com.trading.service.EmailService;
import com.trading.service.TwoFactorOTPService;
import com.trading.utils.OtpUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final CustomerUserDetailsService customerUserDetailsService;
    private final TwoFactorOTPService twoFactorOTPService;
    private final EmailService emailService;
    @Autowired
    public AuthController(UserRepository UserRepository, CustomerUserDetailsService customerUserDetailsService, TwoFactorOTPService twoFactorOTPService, EmailService emailService) {
        this.userRepository = UserRepository;
        this.customerUserDetailsService = customerUserDetailsService;
        this.twoFactorOTPService = twoFactorOTPService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {

        User isEmailExist = userRepository.findByEmail(user.getEmail());
        if (isEmailExist != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User savedUser = new User();
        savedUser.setEmail(user.getEmail());
        savedUser.setFullName(user.getFullName());
        savedUser.setPassword(user.getPassword());
        User savedUserSaved = userRepository.save(savedUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Successfully registered");
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws MessagingException {

        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);
        User authUser = userRepository.findByEmail(username);
        if (user.getTwoFactorAuth().isEnabled()){
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Successfully 2fa in");
            authResponse.setTwoFactorAuthenticated(true);
            String otp = OtpUtils.generateOTP();
            TwoFactorOTP oldTwoFactorOTP = twoFactorOTPService.findByUser(authUser.getId());
            if (oldTwoFactorOTP != null){
                twoFactorOTPService.deleteTwoFactorOTP(oldTwoFactorOTP);
            }
            TwoFactorOTP newTwoFactorOTP = twoFactorOTPService.createTwoFactorOTP(authUser,otp,jwt);
            emailService.sendVerificationEmail(username,otp);
            authResponse.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Successfully logged in");
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!password.equals(userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigInOtp(@PathVariable String otp, @RequestBody String id) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);
        if (twoFactorOTPService.verifyTwoFactorOTP(twoFactorOTP,otp)) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setTwoFactorAuthenticated(true);
            authResponse.setMessage("Successfully verified");
            authResponse.setToken(twoFactorOTP.getJwt());
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        throw new Exception("Invalid otp");
    }
}
