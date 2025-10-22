package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/** Command: Add comment to a post */
public record AddCommentCommand(PostId postId, UserId authorId, String content, String imageUrl) {
    public AddCommentCommand {
        if (postId == null) throw new IllegalArgumentException("postId required");
        if (authorId == null) throw new IllegalArgumentException("authorId required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content required");
        // imageUrl is optional
    }
}

