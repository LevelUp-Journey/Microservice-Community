package com.levelupjourney.microservicecommunity.interaction.domain.model.events;

import com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects.CommentContent;
import com.levelupjourney.microservicecommunity.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event fired when a new comment is added to a post.
 */
public record CommentAdded(
    String commentId,
    String postId,
    String authorId,
    CommentContent content,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public CommentAdded {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment ID cannot be null or empty");
        }
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
        return "CommentAdded";
    }
    
    @Override
    public String aggregateId() {
        return commentId;
    }
}
