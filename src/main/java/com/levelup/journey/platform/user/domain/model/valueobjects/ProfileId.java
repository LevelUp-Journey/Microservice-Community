package com.levelup.journey.platform.user.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Profile ID
 * References the user profile from the Profile bounded context
 */
public record ProfileId(String value) {

    public ProfileId {
        Objects.requireNonNull(value, "ProfileId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ProfileId cannot be empty");
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
            throw new IllegalArgumentException("ProfileId must be a valid UUID: " + value, e);
        }
    }

    /**
     * Creates a ProfileId from a String
     * @param value the UUID value as String
     * @return a new instance of ProfileId
     */
    public static ProfileId of(String value) {
        return new ProfileId(value);
    }

    /**
     * Generates a random ProfileId
     * @return a new instance of ProfileId with a random UUID
     */
    public static ProfileId random() {
        return new ProfileId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
