package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;

/**
 * Query to get all subscriptions for a specific community
 */
public record GetSubscriptionsByCommunityIdQuery(CommunityId communityId) {
    public GetSubscriptionsByCommunityIdQuery {
        if (communityId == null) {
            throw new IllegalArgumentException("Community id is required");
        }
    }
}
