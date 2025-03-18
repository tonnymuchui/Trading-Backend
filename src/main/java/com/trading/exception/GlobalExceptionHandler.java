package com.trading.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // Log the exception (optional but recommended)
        System.err.println("Error: " + e.getMessage());

        // Return a generic error response
        return new ResponseEntity<>(
                "An error occurred: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}