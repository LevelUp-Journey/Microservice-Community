package com.levelup.journey.platform.social.domain.model.aggregates;

import com.levelup.journey.platform.social.domain.model.events.SubscriptionCreated;
import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Subscription aggregate root
 * Represents a user's subscription to a community
 */
public final class Subscription extends AggregateRoot {
    private final SubscriptionId id;
    private final UserId userId;
    private final CommunityId communityId;
    private final Instant createdAt;

    private Subscription(SubscriptionId id, UserId userId, CommunityId communityId, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Subscription id cannot be null");
        this.userId = Objects.requireNonNull(userId, "User id cannot be null");
        this.communityId = Objects.requireNonNull(communityId, "Community id cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    }

    /**
     * Factory method to create a new subscription (emits event)
     */
    public static Subscription create(UserId userId, CommunityId communityId) {
        SubscriptionId id = SubscriptionId.of(UUID.randomUUID().toString());
        Subscription subscription = new Subscription(id, userId, communityId, Instant.now());
        subscription.recordEvent(new SubscriptionCreated(
                id.value(),
                userId.value(),
                communityId.value(),
                subscription.createdAt
        ));
        return subscription;
    }

    /**
     * Factory method to restore subscription from persistence (no events)
     */
    public static Subscription restore(SubscriptionId id, UserId userId, CommunityId communityId, Instant createdAt) {
        return new Subscription(id, userId, communityId, createdAt);
    }

    // Getters
    public SubscriptionId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public CommunityId communityId() {
        return communityId;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
