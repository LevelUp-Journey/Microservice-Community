package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

/**
 * Command to delete a comment.
 */
public record DeleteCommentCommand(
    String commentId,
    String authorId
) {
    
    public DeleteCommentCommand {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment ID cannot be null or empty");
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID cannot be null or empty");
        }
    }
}
