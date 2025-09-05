package com.levelupjourney.microservicecommunity.posting.domain.model.commands;

/**
 * Command to delete a post.
 */
public record DeletePostCommand(
    String postId,
    String deleterId
) {
    
    public DeletePostCommand {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (deleterId == null || deleterId.trim().isEmpty()) {
            throw new IllegalArgumentException("Deleter ID cannot be null or empty");
        }
    }
}
