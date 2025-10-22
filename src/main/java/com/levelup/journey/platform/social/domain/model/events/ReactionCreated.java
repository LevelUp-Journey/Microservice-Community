package com.levelup.journey.platform.social.domain.model.events;

import java.time.Instant;

/**
 * Event emitted when a reaction is created
 */
public record ReactionCreated(
        String aggregateId,
        String postId,
        String userId,
        String reactionType,
        Instant occurredOn
) implements DomainEvent {
    public ReactionCreated {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId is required");
        }
        if (postId == null || postId.isBlank()) {
            throw new IllegalArgumentException("postId is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (reactionType == null || reactionType.isBlank()) {
            throw new IllegalArgumentException("reactionType is required");
        }
        if (occurredOn == null) {
            throw new IllegalArgumentException("occurredOn is required");
        }
    }

    @Override
    public String eventType() {
        return getClass().getSimpleName();
    }
}
