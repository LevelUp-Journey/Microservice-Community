package com.levelup.journey.platform.social.domain.model.events;

import java.time.Instant;

/**
 * Event emitted when a subscription is removed
 */
public record SubscriptionRemoved(
        String aggregateId,
        String userId,
        String communityId,
        Instant occurredOn
) implements DomainEvent {
    public SubscriptionRemoved {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (communityId == null || communityId.isBlank()) {
            throw new IllegalArgumentException("communityId is required");
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
