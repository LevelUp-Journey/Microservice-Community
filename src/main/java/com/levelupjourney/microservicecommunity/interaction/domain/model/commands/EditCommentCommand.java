package com.levelupjourney.microservicecommunity.interaction.domain.model.commands;

import com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects.CommentContent;

/**
 * Command to edit an existing comment.
 */
public record EditCommentCommand(
    String commentId,
    String editorId,
    CommentContent newContent
) {
    
    public EditCommentCommand {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment ID cannot be null or empty");
        }
        if (editorId == null || editorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Editor ID cannot be null or empty");
        }
        if (newContent == null) {
            throw new IllegalArgumentException("New content cannot be null");
        }
    }
}
