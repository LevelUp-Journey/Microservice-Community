package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

/**
 * Command to delete a comment.
 */
public record DeleteCommentCommand(
    String commentId,
    String deleterId
) {
    
    public DeleteCommentCommand {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment ID cannot be null or empty");
        }
        if (deleterId == null || deleterId.trim().isEmpty()) {
            throw new IllegalArgumentException("Deleter ID cannot be null or empty");
        }
    }
}
