package com.levelupjourney.microservicecommunity.shared.domain.validation;

import org.springframework.util.StringUtils;

/**
 * Utility class for domain validation.
 * Provides common validation methods for business rules.
 */
public final class ValidationUtils {
    
    private ValidationUtils() {
        // Utility class
    }
    
    /**
     * Validates that a string is not null, empty, or blank.
     * 
     * @param value the string to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireNonBlank(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be null, empty, or blank");
        }
    }
    
    /**
     * Validates that an object is not null.
     * 
     * @param value the object to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
    
    /**
     * Validates that a string meets minimum length requirements.
     * 
     * @param value the string to validate
     * @param minLength the minimum required length
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireMinLength(String value, int minLength, String fieldName) {
        requireNonBlank(value, fieldName);
        if (value.trim().length() < minLength) {
            throw new IllegalArgumentException(fieldName + " must be at least " + minLength + " characters long");
        }
    }
    
    /**
     * Validates that a string does not exceed maximum length.
     * 
     * @param value the string to validate
     * @param maxLength the maximum allowed length
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
    
    /**
     * Validates that a string is within the specified length range.
     * 
     * @param value the string to validate
     * @param minLength the minimum required length
     * @param maxLength the maximum allowed length
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireLengthRange(String value, int minLength, int maxLength, String fieldName) {
        requireMinLength(value, minLength, fieldName);
        requireMaxLength(value, maxLength, fieldName);
    }
    
    /**
     * Validates that a number is positive (greater than zero).
     * 
     * @param value the number to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requirePositive(long value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive (greater than zero)");
        }
    }
    
    /**
     * Validates that a number is non-negative (greater than or equal to zero).
     * 
     * @param value the number to validate
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireNonNegative(long value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }
    
    /**
     * Validates that a number is within the specified range (inclusive).
     * 
     * @param value the number to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireRange(long value, long min, long max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
    }
}
