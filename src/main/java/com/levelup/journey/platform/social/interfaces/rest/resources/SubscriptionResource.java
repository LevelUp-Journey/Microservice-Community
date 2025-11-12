package com.levelup.journey.platform.social.interfaces.rest.resources;

import java.time.Instant;

/**
 * Subscription Resource
 * Response payload for subscription data
 */
public record SubscriptionResource(
        String id,
        String userId,
        String communityId,
        String communityName,
        String communityImageUrl,
        Instant createdAt
) {
}
