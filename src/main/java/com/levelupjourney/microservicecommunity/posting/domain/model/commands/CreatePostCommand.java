package com.levelupjourney.microservicecommunity.posting.domain.model.commands;

import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.PostBody;

/**
 * Command to create a new post.
 */
public record CreatePostCommand(
    String authorId,
    PostBody content
) {
    
    public CreatePostCommand {
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Author ID cannot be null or empty");
        }
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
    }
}
