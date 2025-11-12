package com.levelup.journey.platform.post.interfaces.rest.resources;

import java.util.Map;

/**
 * Reaction Summary Resource
 * Aggregated reaction data for a post
 */
public record ReactionSummaryResource(
        Map<String, Integer> reactionCounts,
        String userReaction
) {
}
