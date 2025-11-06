package com.levelup.journey.platform.social.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Follow relationship identifier
 */
public record FollowId(String value) {
    public FollowId {
        Objects.requireNonNull(value, "FollowId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("FollowId cannot be empty");
        }
        validateUUID(value);
    }

    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("FollowId must be a valid UUID: " + value, e);
        }
    }

    public static FollowId of(String value) {
        return new FollowId(value);
    }

    public static FollowId random() {
        return new FollowId(UUID.randomUUID().toString());
    }
}
