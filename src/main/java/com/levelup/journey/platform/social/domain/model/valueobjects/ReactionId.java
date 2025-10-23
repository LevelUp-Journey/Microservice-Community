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
        // Allow synthetic ID format: uuid-uuid (for composite key reactions)
        String[] parts = value.split("-");
        
        if (parts.length == 5) {
            // Standard UUID format (8-4-4-4-12)
            try {
                UUID.fromString(value);
                return; // Valid UUID format
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("ReactionId must be a valid UUID: " + value, e);
            }
        } else if (parts.length == 10) {
            // Synthetic ID: uuid-uuid (8-4-4-4-12-8-4-4-4-12)
            // Reconstruct both UUIDs and validate them
            String firstUUID = String.join("-", parts[0], parts[1], parts[2], parts[3], parts[4]);
            String secondUUID = String.join("-", parts[5], parts[6], parts[7], parts[8], parts[9]);
            try {
                UUID.fromString(firstUUID);
                UUID.fromString(secondUUID);
                return; // Valid synthetic ID format
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("ReactionId synthetic format must contain two valid UUIDs: " + value, e);
            }
        }

        throw new IllegalArgumentException("ReactionId must be a valid UUID or synthetic UUID-UUID format: " + value);
    }

    public static ReactionId of(String value) {
        return new ReactionId(value);
    }

    public static ReactionId random() {
        return new ReactionId(UUID.randomUUID().toString());
    }
}
