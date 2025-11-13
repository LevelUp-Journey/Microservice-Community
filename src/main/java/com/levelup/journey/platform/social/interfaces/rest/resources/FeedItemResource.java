package com.levelup.journey.platform.social.interfaces.rest.resources;

import com.levelup.journey.platform.post.interfaces.rest.resources.ReactionSummaryResource;

import java.time.Instant;

/**
 * Feed Item Resource
 * Response payload for a single feed item with enriched post and community metadata
 */
public record FeedItemResource(
        String id,
        String communityId,
        String communityName,
        String communityImageUrl,
        String authorId,
        String authorProfileId,
        String authorName,
        String authorProfileUrl,
        String content,
        String imageUrl,
        Instant createdAt,
        ReactionSummaryResource reactions
) {
}
