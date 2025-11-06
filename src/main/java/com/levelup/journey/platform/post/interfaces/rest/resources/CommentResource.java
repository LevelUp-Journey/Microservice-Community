package com.levelup.journey.platform.post.interfaces.rest.resources;

import java.time.Instant;

/**
 * Comment Resource
 * Response payload for comment data
 */
public record CommentResource(
        String id,
        String authorId,
        String authorProfileId,
        String content,
        String imageUrl,
        Instant createdAt
) {
}
