package com.levelup.journey.platform.social.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reaction is removed
 */
public class ReactionRemoved implements DomainEvent {
    private final String aggregateId;
    private final String postId;
    private final String userId;
    private final Instant occurredOn;

    public ReactionRemoved(String aggregateId, String postId, String userId, Instant occurredOn) {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId is required");
        }
        if (postId == null || postId.isBlank()) {
            throw new IllegalArgumentException("postId is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (occurredOn == null) {
            throw new IllegalArgumentException("occurredOn is required");
        }
        this.aggregateId = aggregateId;
        this.postId = postId;
        this.userId = userId;
        this.occurredOn = occurredOn;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
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

        ReactionRemoved that = (ReactionRemoved) o;

        if (!aggregateId.equals(that.aggregateId)) return false;
        if (!postId.equals(that.postId)) return false;
        if (!userId.equals(that.userId)) return false;
        return occurredOn.equals(that.occurredOn);
    }

    @Override
    public int hashCode() {
        int result = aggregateId.hashCode();
        result = 31 * result + postId.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + occurredOn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ReactionRemoved{" +
                "aggregateId='" + aggregateId + '\'' +
                ", postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
