package com.levelup.journey.platform.user.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a User ID
 * Identifies users uniquely in the system
 */
public record UserId(String value) {

    public UserId {
        Objects.requireNonNull(value, "UserId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be empty");
        }
        validateUUID(value);
    }

    /**
     * Validates that the value is a valid UUID
     * @param value the value to validate
     * @throws IllegalArgumentException if the value is not a valid UUID
     */
    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("UserId must be a valid UUID: " + value, e);
        }
    }

    /**
     * Creates a UserId from a String
     * @param value the UUID value as String
     * @return a new instance of UserId
     */
    public static UserId of(String value) {
        return new UserId(value);
    }

    /**
     * Generates a random UserId
     * @return a new instance of UserId with a random UUID
     */
    public static UserId random() {
        return new UserId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
