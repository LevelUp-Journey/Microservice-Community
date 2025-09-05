package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

import com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects.CommentContent;

/**
 * Command to add a comment to a post.
 */
public record AddCommentCommand(
    String postId,
    String authorId,
    CommentContent content
) {
    
    public AddCommentCommand {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID cannot be null or empty");
        }
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
    }
}
