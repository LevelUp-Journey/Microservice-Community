package com.levelupjourney.microservicecommunity.interaction.domain.model.events;

import com.levelupjourney.microservicecommunity.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event fired when a comment is deleted.
 */
public record CommentDeleted(
    String commentId,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public CommentDeleted {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment ID cannot be null or empty");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    @Override
    public String eventType() {
        return "CommentDeleted";
    }
    
    @Override
    public String aggregateId() {
        return commentId;
    }
}
