package com.levelup.journey.platform.social.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Reaction identifier
 */
public record ReactionId(String value) {
    public ReactionId {
        Objects.requireNonNull(value, "ReactionId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ReactionId cannot be empty");
        }
        validateUUID(value);
    }

    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ReactionId must be a valid UUID: " + value, e);
        }
    }

    public static ReactionId of(String value) {
        return new ReactionId(value);
    }

    public static ReactionId random() {
        return new ReactionId(UUID.randomUUID().toString());
    }
}
