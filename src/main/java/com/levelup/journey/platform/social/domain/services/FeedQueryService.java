package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.queries.GetFeedByUserIdQuery;
import com.levelup.journey.platform.social.interfaces.rest.resources.PaginatedFeedResource;

/**
 * Feed Query Service
 * Defines read operations for user feeds
 */
public interface FeedQueryService {

    /**
     * Handles the query to get a user's personalized feed with enriched post and community metadata
     * Returns paginated feed items with complete post information including community details
     * @param query the get feed by user id query
     * @return paginated feed resource with enriched post data
     */
    PaginatedFeedResource handle(GetFeedByUserIdQuery query);
}
