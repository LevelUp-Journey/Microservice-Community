package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

/**
 * Command to edit an existing comment.
 */
public record EditCommentCommand(
    String commentId,
    String authorId,
    String newContent
) {
    
    public EditCommentCommand {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment ID cannot be null or empty");
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID cannot be null or empty");
        }
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("New content cannot be null or empty");
        }
    }
}
