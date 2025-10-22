package com.levelup.journey.platform.post.domain.model.events;

import java.time.Instant;

/** Event emitted when a comment is added to a post */
public record CommentAdded(String aggregateId, String commentId, String authorId, Instant occurredOn)
        implements DomainEvent {
    public CommentAdded {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (commentId == null || commentId.isBlank()) throw new IllegalArgumentException("commentId required");
        if (authorId == null || authorId.isBlank()) throw new IllegalArgumentException("authorId required");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn required");
    }
    @Override public String eventType() { return getClass().getSimpleName(); }
}

