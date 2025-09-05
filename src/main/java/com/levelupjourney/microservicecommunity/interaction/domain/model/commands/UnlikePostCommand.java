package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

/**
 * Command to unlike a post.
 */
public record UnlikePostCommand(
    String postId,
    String userId
) {
    
    public UnlikePostCommand {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}
