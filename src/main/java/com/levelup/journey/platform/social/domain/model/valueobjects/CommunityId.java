package com.levelup.journey.platform.social.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Community identifier from Post bounded context
 */
public record CommunityId(String value) {
    public CommunityId {
        Objects.requireNonNull(value, "CommunityId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CommunityId cannot be empty");
        }
        validateUUID(value);
    }

    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("CommunityId must be a valid UUID: " + value, e);
        }
    }

    public static CommunityId of(String value) {
        return new CommunityId(value);
    }
}
