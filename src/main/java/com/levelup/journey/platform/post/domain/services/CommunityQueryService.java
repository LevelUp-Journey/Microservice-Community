package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.domain.model.queries.GetAllCommunitiesQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Community Query Service
 * Defines read operations for communities
 */
public interface CommunityQueryService {

    /**
     * Handles the query to get a community by its ID
     * @param query the get community by id query
     * @return the community if found
     */
    Optional<Community> handle(GetCommunityByIdQuery query);

    /**
     * Handles the query to get all communities
     * @param query the get all communities query
     * @return list of all communities
     */
    List<Community> handle(GetAllCommunitiesQuery query);
}
