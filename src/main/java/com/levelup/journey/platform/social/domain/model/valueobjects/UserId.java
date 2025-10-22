package com.levelup.journey.platform.social.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a User identifier
 */
public record UserId(String value) {
    public UserId {
        Objects.requireNonNull(value, "UserId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be empty");
        }
        validateUUID(value);
    }

    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("UserId must be a valid UUID: " + value, e);
        }
    }

    public static UserId of(String value) {
        return new UserId(value);
    }
}
