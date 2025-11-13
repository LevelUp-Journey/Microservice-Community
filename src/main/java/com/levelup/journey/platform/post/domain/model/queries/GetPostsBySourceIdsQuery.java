package com.levelup.journey.platform.post.domain.model.queries;

import java.util.List;

/**
 * Query to get posts by source IDs (author IDs or community IDs) with pagination
 * This query is used to fetch posts from multiple sources for feed generation
 */
public record GetPostsBySourceIdsQuery(
        List<String> sourceIds,
        int limit,
        int offset
) {
    public GetPostsBySourceIdsQuery {
        if (sourceIds == null) {
            throw new IllegalArgumentException("sourceIds cannot be null");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be non-negative");
        }
    }
}
