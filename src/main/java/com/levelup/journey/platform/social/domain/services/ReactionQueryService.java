package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByPostIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetReactionsByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Reaction Query Service
 * Defines read operations for reactions
 */
public interface ReactionQueryService {

    /**
     * Handles the query to get a reaction by its ID
     * @param query the get reaction by id query
     * @return the reaction if found
     */
    Optional<Reaction> handle(GetReactionByIdQuery query);

    /**
     * Handles the query to get reactions by post ID
     * @param query the get reactions by post id query
     * @return list of reactions for the post
     */
    List<Reaction> handle(GetReactionsByPostIdQuery query);

    /**
     * Handles the query to get reactions by user ID
     * @param query the get reactions by user id query
     * @return list of reactions made by the user
     */
    List<Reaction> handle(GetReactionsByUserIdQuery query);
}
