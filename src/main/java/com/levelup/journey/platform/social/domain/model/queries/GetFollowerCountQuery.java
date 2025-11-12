package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get the follower count for a user
 */
public record GetFollowerCountQuery(UserId userId) {
    public GetFollowerCountQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }
}