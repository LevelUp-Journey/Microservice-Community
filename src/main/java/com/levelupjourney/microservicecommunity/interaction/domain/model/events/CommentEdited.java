package com.levelupjourney.microservicecommunity.interaction.domain.model.events;

import com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects.CommentContent;
import com.levelupjourney.microservicecommunity.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event fired when a comment is edited.
 */
public record CommentEdited(
    String commentId,
    CommentContent newContent,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public CommentEdited {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment ID cannot be null or empty");
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
        return "CommentEdited";
    }
    
    @Override
    public String aggregateId() {
        return commentId;
    }
}
