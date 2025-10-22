package com.levelup.journey.platform.post.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

/**
 * Post Resource
 * Response payload for post data
 */
public record PostResource(
        String id,
        String communityId,
        String authorId,
        String title,
        String content,
        String imageUrl,
        Instant createdAt,
        List<CommentResource> comments
) {
}
