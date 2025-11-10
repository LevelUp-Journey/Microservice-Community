package com.levelup.journey.platform.user.domain.services;

import com.levelup.journey.platform.user.domain.model.aggregates.User;
import com.levelup.journey.platform.user.domain.model.queries.GetUserByUserIdQuery;

import java.util.Optional;

/**
 * Query service interface for User operations
 */
public interface UserQueryService {

    /**
     * Handles the query to get a user by user ID
     * @param query the query with user ID
     * @return an Optional containing the user if found
     */
    Optional<User> handle(GetUserByUserIdQuery query);
}
