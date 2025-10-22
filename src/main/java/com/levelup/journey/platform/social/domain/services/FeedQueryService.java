package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.queries.GetFeedByUserIdQuery;

import java.util.List;

/**
 * Feed Query Service
 * Defines read operations for user feeds
 */
public interface FeedQueryService {

    /**
     * Handles the query to get a user's personalized feed
     * Returns a list of post IDs that should appear in the user's feed
     * @param query the get feed by user id query
     * @return list of post IDs for the feed
     */
    List<String> handle(GetFeedByUserIdQuery query);
}
