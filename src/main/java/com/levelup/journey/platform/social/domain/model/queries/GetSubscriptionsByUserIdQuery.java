package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;

/**
 * Query to get paginated subscriptions for a specific user
 */
public record GetSubscriptionsByUserIdQuery(UserId userId, int page, int size) {
    public GetSubscriptionsByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }

    public GetSubscriptionsByUserIdQuery(UserId userId) {
        this(userId, 0, 20);
    }
}
