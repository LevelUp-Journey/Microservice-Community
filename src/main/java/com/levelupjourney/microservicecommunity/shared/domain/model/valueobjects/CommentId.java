package com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects;

import org.springframework.util.Assert;

/**
 * Value object representing a Comment identifier.
 * Wraps a String ID for domain purity and type safety.
 */
public record CommentId(String value) {
    
    public CommentId {
        Assert.hasText(value, "CommentId value cannot be null or empty");
        // Validate UUID format if needed
        if (!value.matches("^[0-9a-fA-F-]{36}$")) {
            throw new IllegalArgumentException("CommentId must be a valid UUID string");
        }
    }
    
    public static CommentId of(String value) {
        return new CommentId(value);
    }
    
    public static CommentId generate() {
        return new CommentId(java.util.UUID.randomUUID().toString());
    }
    
    @Override
    public String toString() {
        return value;
    }
}
