package com.trading.controller;

import com.trading.modal.User;
import com.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    @Autowired
    public AuthController(UserRepository UserRepository) {
        this.userRepository = UserRepository;
    }
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = new User();
        savedUser.setEmail(user.getEmail());
        savedUser.setFullName(user.getFullName());
        savedUser.setPassword(user.getPassword());
        User savedUserSaved = userRepository.save(savedUser);
        return new ResponseEntity<>(savedUserSaved, HttpStatus.CREATED);
    }
}
