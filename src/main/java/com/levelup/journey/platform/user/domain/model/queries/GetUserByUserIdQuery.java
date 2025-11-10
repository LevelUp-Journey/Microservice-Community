package com.levelup.journey.platform.user.domain.model.queries;

import com.levelup.journey.platform.user.domain.model.valueobjects.UserId;

/**
 * Query to retrieve a user by their user ID
 *
 * @param userId the user ID to search for
 */
public record GetUserByUserIdQuery(UserId userId) {
    public GetUserByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
    }
}
