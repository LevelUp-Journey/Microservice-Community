package com.levelup.journey.platform.social.interfaces.rest.resources;

import java.time.Instant;

/**
 * Reaction Resource
 * Response payload for reaction data
 */
public record ReactionResource(
        String id,
        String postId,
        String userId,
        String reactionType,
        Instant createdAt
) {
}
