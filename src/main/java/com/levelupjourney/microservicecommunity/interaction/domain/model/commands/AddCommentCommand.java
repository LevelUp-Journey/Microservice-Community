package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

/**
 * Command to add a comment to a post.
 */
public record AddCommentCommand(
    String postId,
    String authorId,
    String content
) {
    
    public AddCommentCommand {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID cannot be null or empty");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
    }
}
