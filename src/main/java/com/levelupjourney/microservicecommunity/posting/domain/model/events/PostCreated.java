package com.levelupjourney.microservicecommunity.posting.domain.model.events;

import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.PostBody;
import com.levelupjourney.microservicecommunity.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event fired when a new post is created.
 */
public record PostCreated(
    String postId,
    String authorId,
    PostBody content,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public PostCreated {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID cannot be null or empty");
        }
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    @Override
    public String eventType() {
        return "PostCreated";
    }
    
    @Override
    public String aggregateId() {
        return postId;
    }
}
