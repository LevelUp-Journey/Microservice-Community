package com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects;

import org.springframework.util.Assert;

/**
 * Value object representing a User identifier.
 * Wraps a String ID for domain purity and type safety.
 * This represents users from the external IAM context.
 */
public record UserId(String value) {
    
    public UserId {
        Assert.hasText(value, "UserId value cannot be null or empty");
        // Validate UUID format if needed
        if (!value.matches("^[0-9a-fA-F-]{36}$")) {
            throw new IllegalArgumentException("UserId must be a valid UUID string");
        }
    }
    
    public static UserId of(String value) {
        return new UserId(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
