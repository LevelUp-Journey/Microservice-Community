package com.levelupjourney.microservicecommunity.posting.domain.model.events;

import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.PostBody;
import com.levelupjourney.microservicecommunity.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event fired when a post is edited.
 */
public record PostEdited(
    String postId,
    PostBody newContent,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public PostEdited {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (newContent == null) {
            throw new IllegalArgumentException("New content cannot be null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    @Override
    public String eventType() {
        return "PostEdited";
    }
    
    @Override
    public String aggregateId() {
        return postId;
    }
}
