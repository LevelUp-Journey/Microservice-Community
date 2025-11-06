package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get all followers of a specific user
 */
public record GetFollowersByUserIdQuery(UserId followingId) {
    public GetFollowersByUserIdQuery {
        if (followingId == null) {
            throw new IllegalArgumentException("Following id is required");
        }
    }
}
