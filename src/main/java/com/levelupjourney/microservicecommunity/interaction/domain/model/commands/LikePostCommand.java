package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

/**
 * Command to like a post.
 */
public record LikePostCommand(
    String postId,
    String userId
) {
    
    public LikePostCommand {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}
