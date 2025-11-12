package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get a follow relationship by follower and following user IDs
 */
public record GetFollowByUsersQuery(UserId followerId, UserId followingId) {
    public GetFollowByUsersQuery {
        if (followerId == null) {
            throw new IllegalArgumentException("Follower ID is required");
        }
        if (followingId == null) {
            throw new IllegalArgumentException("Following ID is required");
        }
    }
}