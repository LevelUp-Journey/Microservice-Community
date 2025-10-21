package com.levelup.journey.platform.domain.model.commands;

import com.levelup.journey.platform.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

/** Command: Add comment to a post */
public record AddCommentCommand(PostId postId, CommentId commentId, UserId authorId, String content) {
    public AddCommentCommand {
        if (postId == null) throw new IllegalArgumentException("postId required");
        if (commentId == null) throw new IllegalArgumentException("commentId required");
        if (authorId == null) throw new IllegalArgumentException("authorId required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content required");
    }
}

