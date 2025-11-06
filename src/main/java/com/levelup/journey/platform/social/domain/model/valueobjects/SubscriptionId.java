package com.levelup.journey.platform.social.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Subscription identifier
 */
public record SubscriptionId(String value) {
    public SubscriptionId {
        Objects.requireNonNull(value, "SubscriptionId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SubscriptionId cannot be empty");
        }
        validateUUID(value);
    }

    private static void validateUUID(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("SubscriptionId must be a valid UUID: " + value, e);
        }
    }

    public static SubscriptionId of(String value) {
        return new SubscriptionId(value);
    }

    public static SubscriptionId random() {
        return new SubscriptionId(UUID.randomUUID().toString());
    }
}
