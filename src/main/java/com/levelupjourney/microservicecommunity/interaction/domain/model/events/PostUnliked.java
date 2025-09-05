package com.levelupjourney.microservicecommunity.interaction.domain.model.events;

import com.levelupjourney.microservicecommunity.shared.domain.model.events.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event fired when a user unlikes a post.
 */
public record PostUnliked(
    String postId,
    String userId,
    LocalDateTime occurredAt
) implements DomainEvent {
    
    public PostUnliked {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    @Override
    public String eventType() {
        return "PostUnliked";
    }
    
    @Override
    public String aggregateId() {
        return postId + ":" + userId; // Composite key
    }
}
