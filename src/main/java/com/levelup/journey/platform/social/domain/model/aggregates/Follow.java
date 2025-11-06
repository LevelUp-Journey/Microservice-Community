package com.levelup.journey.platform.social.domain.model.aggregates;

import com.levelup.journey.platform.social.domain.model.events.FollowCreated;
import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.Objects;

/**
 * Follow aggregate root
 * Represents a follow relationship between two users
 */
public final class Follow extends AggregateRoot {
    private final FollowId id;
    private final UserId followerId;
    private final UserId followingId;
    private final Instant createdAt;

    private Follow(FollowId id, UserId followerId, UserId followingId, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Follow id cannot be null");
        this.followerId = Objects.requireNonNull(followerId, "Follower id cannot be null");
        this.followingId = Objects.requireNonNull(followingId, "Following id cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");

        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }
    }

    /**
     * Factory method to create a new follow relationship (emits event)
     */
    public static Follow create(FollowId id, UserId followerId, UserId followingId) {
        Follow follow = new Follow(id, followerId, followingId, Instant.now());
        follow.recordEvent(new FollowCreated(
                id.value(),
                followerId.value(),
                followingId.value(),
                follow.createdAt
        ));
        return follow;
    }

    /**
     * Factory method to restore follow from persistence (no events)
     */
    public static Follow restore(FollowId id, UserId followerId, UserId followingId, Instant createdAt) {
        return new Follow(id, followerId, followingId, createdAt);
    }

    // Getters
    public FollowId id() {
        return id;
    }

    public UserId followerId() {
        return followerId;
    }

    public UserId followingId() {
        return followingId;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
