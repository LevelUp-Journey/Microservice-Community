package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

import java.util.Objects;

/**
 * Query to get a reaction by user ID and post ID
 */
public record GetReactionByUserAndPostQuery(UserId userId, PostId postId) {
    public GetReactionByUserAndPostQuery {
        Objects.requireNonNull(userId, "User ID is required");
        Objects.requireNonNull(postId, "Post ID is required");
    }
}
