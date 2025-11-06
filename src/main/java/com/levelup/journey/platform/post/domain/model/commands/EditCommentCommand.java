package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/** Command: Edit comment content */
public record EditCommentCommand(PostId postId, CommentId commentId, UserId requesterId, String newContent, String newImageUrl) {
    public EditCommentCommand {
        if (postId == null) throw new IllegalArgumentException("postId required");
        if (commentId == null) throw new IllegalArgumentException("commentId required");
        if (requesterId == null) throw new IllegalArgumentException("requesterId required");
        if (newContent == null || newContent.isBlank()) throw new IllegalArgumentException("newContent required");
        // newImageUrl is optional
    }
}