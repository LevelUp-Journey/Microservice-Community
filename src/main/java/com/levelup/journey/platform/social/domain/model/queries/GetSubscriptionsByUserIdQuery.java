package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get all subscriptions for a specific user
 */
public record GetSubscriptionsByUserIdQuery(UserId userId) {
    public GetSubscriptionsByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
    }
}
