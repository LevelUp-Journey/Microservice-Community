package com.levelup.journey.platform.post.interfaces.rest.resources;

import java.time.Instant;

/**
 * Post Resource
 * Response payload for post data
 */
public record PostResource(
        String id,
        String communityId,
        String authorId,
        String authorProfileId,
        String content,
        String imageUrl,
        Instant createdAt
) {
}
