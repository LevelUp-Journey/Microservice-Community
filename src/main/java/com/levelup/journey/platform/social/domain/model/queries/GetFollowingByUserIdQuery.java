package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get all users that a specific user is following
 */
public record GetFollowingByUserIdQuery(UserId followerId) {
    public GetFollowingByUserIdQuery {
        if (followerId == null) {
            throw new IllegalArgumentException("Follower id is required");
        }
    }
}
