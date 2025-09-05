package com.levelupjourney.microservicecommunity.posting.domain.model.events;

import com.levelupjourney.microservicecommunity.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event fired when a post is deleted.
 */
public record PostDeleted(
    String postId,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public PostDeleted {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    @Override
    public String eventType() {
        return "PostDeleted";
    }
    
    @Override
    public String aggregateId() {
        return postId;
    }
}
