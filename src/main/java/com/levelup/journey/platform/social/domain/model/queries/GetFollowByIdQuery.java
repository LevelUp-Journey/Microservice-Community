package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;

/**
 * Query to get a follow relationship by its ID
 */
public record GetFollowByIdQuery(FollowId id) {
    public GetFollowByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Follow id is required");
        }
    }
}
