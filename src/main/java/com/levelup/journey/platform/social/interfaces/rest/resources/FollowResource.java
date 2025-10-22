package com.levelup.journey.platform.social.interfaces.rest.resources;

import java.time.Instant;

/**
 * Follow Resource
 * Response payload for follow relationship data
 */
public record FollowResource(
        String id,
        String followerId,
        String followingId,
        Instant createdAt
) {
}
