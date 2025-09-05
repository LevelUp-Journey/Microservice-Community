package com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects;

import org.springframework.util.Assert;

/**
 * Value object representing a Post identifier.
 * Wraps a String ID for domain purity and type safety.
 */
public record PostId(String value) {
    
    public PostId {
        Assert.hasText(value, "PostId value cannot be null or empty");
        // Validate UUID format if needed
        if (!value.matches("^[0-9a-fA-F-]{36}$")) {
            throw new IllegalArgumentException("PostId must be a valid UUID string");
        }
    }
    
    public static PostId of(String value) {
        return new PostId(value);
    }
    
    public static PostId generate() {
        return new PostId(java.util.UUID.randomUUID().toString());
    }
    
    @Override
    public String toString() {
        return value;
    }
}
