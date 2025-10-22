package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get all reactions made by a specific user
 */
public record GetReactionsByUserIdQuery(UserId userId) {
    public GetReactionsByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
    }
}
