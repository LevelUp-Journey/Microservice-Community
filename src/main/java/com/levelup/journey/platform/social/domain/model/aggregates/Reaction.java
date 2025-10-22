package com.levelup.journey.platform.social.domain.model.aggregates;

import com.levelup.journey.platform.social.domain.model.events.ReactionCreated;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Reaction aggregate root
 * Represents a user's reaction to a post
 */
public final class Reaction extends AggregateRoot {
    private final ReactionId id;
    private final PostId postId;
    private final UserId userId;
    private final ReactionType reactionType;
    private final Instant createdAt;

    private Reaction(ReactionId id, PostId postId, UserId userId, ReactionType reactionType, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Reaction id cannot be null");
        this.postId = Objects.requireNonNull(postId, "Post id cannot be null");
        this.userId = Objects.requireNonNull(userId, "User id cannot be null");
        this.reactionType = Objects.requireNonNull(reactionType, "Reaction type cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    }

    /**
     * Factory method to create a new reaction (emits event)
     */
    public static Reaction create(ReactionId id, PostId postId, UserId userId, ReactionType reactionType) {
        Reaction reaction = new Reaction(id, postId, userId, reactionType, Instant.now());
        reaction.recordEvent(new ReactionCreated(
                id.value(),
                postId.value(),
                userId.value(),
                reactionType.name(),
                reaction.createdAt
        ));
        return reaction;
    }

    /**
     * Factory method to restore reaction from persistence (no events)
     */
    public static Reaction restore(ReactionId id, PostId postId, UserId userId, ReactionType reactionType, Instant createdAt) {
        return new Reaction(id, postId, userId, reactionType, createdAt);
    }

    // Getters
    public ReactionId id() {
        return id;
    }

    public PostId postId() {
        return postId;
    }

    public UserId userId() {
        return userId;
    }

    public ReactionType reactionType() {
        return reactionType;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
