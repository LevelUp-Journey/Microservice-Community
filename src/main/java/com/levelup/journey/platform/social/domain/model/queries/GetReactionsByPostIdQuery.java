package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;

/**
 * Query to get all reactions for a specific post
 */
public record GetReactionsByPostIdQuery(PostId postId) {
    public GetReactionsByPostIdQuery {
        if (postId == null) {
            throw new IllegalArgumentException("Post id is required");
        }
    }
}
