package com.levelupjourney.microservicecommunity.shared.interfaces.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Community microservice.
 * Handles business rule violations and authorization errors.
 * Temporarily disabled to debug startup issues.
 */
//@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException ex) {
        return createErrorResponse(
            HttpStatus.FORBIDDEN,
            "AUTHORIZATION_ERROR",
            ex.getMessage(),
            "User is not authorized to perform this action"
        );
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_ARGUMENT",
            ex.getMessage(),
            "The provided argument is invalid"
        );
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        return createErrorResponse(
            HttpStatus.CONFLICT,
            "INVALID_STATE",
            ex.getMessage(),
            "The current state does not allow this operation"
        );
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            "Please contact support if the problem persists"
        );
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(
        HttpStatus status, 
        String errorCode, 
        String message, 
        String details
    ) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("code", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("details", details);
        
        return new ResponseEntity<>(errorResponse, status);
    }
}
