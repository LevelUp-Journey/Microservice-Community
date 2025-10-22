package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get the feed for a specific user
 * Feed includes posts from followed users and subscribed communities
 */
public record GetFeedByUserIdQuery(UserId userId, int limit, int offset) {
    public GetFeedByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
    }

    public GetFeedByUserIdQuery(UserId userId) {
        this(userId, 20, 0);
    }
}
