package com.levelupjourney.microservicecommunity.posting.domain.model.commands;

import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.PostBody;

/**
 * Command to edit an existing post.
 */
public record EditPostCommand(
    String postId,
    String editorId,
    PostBody newContent
) {
    
    public EditPostCommand {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
        if (editorId == null || editorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Editor ID cannot be null or empty");
        }
        if (newContent == null) {
            throw new IllegalArgumentException("New content cannot be null");
        }
    }
}
