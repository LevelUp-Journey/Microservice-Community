package com.levelup.journey.platform.post.interfaces.rest.resources;

import java.time.Instant;

/**
 * Community Resource
 * Response payload for community data
 */
public record CommunityResource(
        String id,
        String ownerId,
        String name,
        String description,
        String imageUrl,
        Instant createdAt
) {
}
