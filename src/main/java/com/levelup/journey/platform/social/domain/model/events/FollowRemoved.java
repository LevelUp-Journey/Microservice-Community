package com.levelup.journey.platform.social.domain.model.events;

import java.time.Instant;

/**
 * Event emitted when a follow relationship is removed
 */
public record FollowRemoved(
        String aggregateId,
        String followerId,
        String followingId,
        Instant occurredOn
) implements DomainEvent {
    public FollowRemoved {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId is required");
        }
        if (followerId == null || followerId.isBlank()) {
            throw new IllegalArgumentException("followerId is required");
        }
        if (followingId == null || followingId.isBlank()) {
            throw new IllegalArgumentException("followingId is required");
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
