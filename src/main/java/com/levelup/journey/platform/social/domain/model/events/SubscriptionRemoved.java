package com.levelup.journey.platform.social.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a subscription is removed
 */
public class SubscriptionRemoved implements DomainEvent {
    private final String aggregateId;
    private final String userId;
    private final String communityId;
    private final Instant occurredOn;

    public SubscriptionRemoved(String aggregateId, String userId, String communityId, Instant occurredOn) {
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
        this.aggregateId = aggregateId;
        this.userId = userId;
        this.communityId = communityId;
        this.occurredOn = occurredOn;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionRemoved that = (SubscriptionRemoved) o;

        if (!aggregateId.equals(that.aggregateId)) return false;
        if (!userId.equals(that.userId)) return false;
        if (!communityId.equals(that.communityId)) return false;
        return occurredOn.equals(that.occurredOn);
    }

    @Override
    public int hashCode() {
        int result = aggregateId.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + communityId.hashCode();
        result = 31 * result + occurredOn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SubscriptionRemoved{" +
                "aggregateId='" + aggregateId + '\'' +
                ", userId='" + userId + '\'' +
                ", communityId='" + communityId + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
