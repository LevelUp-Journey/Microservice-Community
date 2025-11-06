package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.SubscriptionId;

/**
 * Query to get a subscription by its ID
 */
public record GetSubscriptionByIdQuery(SubscriptionId id) {
    public GetSubscriptionByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Subscription id is required");
        }
    }
}
