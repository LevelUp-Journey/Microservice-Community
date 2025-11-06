package com.levelup.journey.platform.social.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a follow relationship is removed
 */
public class FollowRemoved implements DomainEvent {
    private final String aggregateId;
    private final String followerId;
    private final String followingId;
    private final Instant occurredOn;

    public FollowRemoved(String aggregateId, String followerId, String followingId, Instant occurredOn) {
        this.aggregateId = aggregateId;
        this.followerId = followerId;
        this.followingId = followingId;
        this.occurredOn = occurredOn;

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
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public String eventType() {
        return getClass().getSimpleName();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public String followerId() {
        return followerId;
    }

    public String followingId() {
        return followingId;
    }
}
