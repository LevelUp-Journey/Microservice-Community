package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/**
 * Delete Post Command
 * Command to delete a post with authorization checks
 */
public record DeletePostCommand(PostId postId, UserId requesterId) {

    public DeletePostCommand {
        if (postId == null) throw new IllegalArgumentException("postId cannot be null");
        if (requesterId == null) throw new IllegalArgumentException("requesterId cannot be null");
    }

    public static DeletePostCommand of(String postId, String requesterId) {
        return new DeletePostCommand(PostId.of(postId), UserId.of(requesterId));
    }
}