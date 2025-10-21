package com.levelup.journey.platform.domain.model.commands;

import com.levelup.journey.platform.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

/** Command: Agregar comentario a un post */
public record AddCommentCommand(PostId postId, CommentId commentId, UserId authorId, String content) {
    public AddCommentCommand {
        if (postId == null) throw new IllegalArgumentException("postId requerido");
        if (commentId == null) throw new IllegalArgumentException("commentId requerido");
        if (authorId == null) throw new IllegalArgumentException("authorId requerido");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content requerido");
    }
}

