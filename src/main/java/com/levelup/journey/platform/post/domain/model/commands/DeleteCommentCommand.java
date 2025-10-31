package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/** Command: Delete comment from a post */
public record DeleteCommentCommand(PostId postId, CommentId commentId, UserId requesterId) {
    public DeleteCommentCommand {
        if (postId == null) throw new IllegalArgumentException("postId required");
        if (commentId == null) throw new IllegalArgumentException("commentId required");
        if (requesterId == null) throw new IllegalArgumentException("requesterId required");
    }
}