package com.levelup.journey.platform.social.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Feed Entry identifier
 */
public record FeedEntryId(String value) {
    public FeedEntryId {
        Objects.requireNonNull(value, "FeedEntryId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("FeedEntryId cannot be empty");
        }
        validateUUID(value);
    }

    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("FeedEntryId must be a valid UUID: " + value, e);
        }
    }

    public static FeedEntryId of(String value) {
        return new FeedEntryId(value);
    }

    public static FeedEntryId random() {
        return new FeedEntryId(UUID.randomUUID().toString());
    }
}
