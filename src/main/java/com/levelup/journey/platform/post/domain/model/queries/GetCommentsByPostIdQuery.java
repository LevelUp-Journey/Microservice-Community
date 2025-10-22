package com.levelup.journey.platform.post.domain.model.queries;

import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;

/**
 * Query: Get all comments for a specific post
 */
public record GetCommentsByPostIdQuery(PostId postId) {
    public GetCommentsByPostIdQuery {
        if (postId == null) throw new IllegalArgumentException("postId required");
    }
}
