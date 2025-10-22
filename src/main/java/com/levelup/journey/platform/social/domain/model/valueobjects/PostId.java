package com.levelup.journey.platform.social.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Post identifier from Post bounded context
 */
public record PostId(String value) {
    public PostId {
        Objects.requireNonNull(value, "PostId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("PostId cannot be empty");
        }
        validateUUID(value);
    }

    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("PostId must be a valid UUID: " + value, e);
        }
    }

    public static PostId of(String value) {
        return new PostId(value);
    }
}
