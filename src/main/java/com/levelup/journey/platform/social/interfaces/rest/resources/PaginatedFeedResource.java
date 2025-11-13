package com.levelup.journey.platform.social.interfaces.rest.resources;

import java.util.List;

/**
 * Paginated Feed Resource
 * Response payload for paginated feed with enriched post and community metadata
 */
public record PaginatedFeedResource(
        List<FeedItemResource> content,
        int page,
        int size,
        long totalElements
) {
}
