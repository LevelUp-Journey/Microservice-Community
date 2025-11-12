package com.levelup.journey.platform.social.domain.services;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowByIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowByUsersQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowerCountQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowersByUserIdQuery;
import com.levelup.journey.platform.social.domain.model.queries.GetFollowingByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Follow Query Service
 * Defines read operations for follow relationships
 */
public interface FollowQueryService {

    /**
     * Handles the query to get a follow relationship by its ID
     * @param query the get follow by id query
     * @return the follow relationship if found
     */
    Optional<Follow> handle(GetFollowByIdQuery query);

    /**
     * Handles the query to get followers of a user
     * @param query the get followers by user id query
     * @return list of followers
     */
    List<Follow> handle(GetFollowersByUserIdQuery query);

    /**
     * Handles the query to get users a user is following
     * @param query the get following by user id query
     * @return list of following relationships
     */
    List<Follow> handle(GetFollowingByUserIdQuery query);

    /**
     * Handles the query to get a follow relationship by follower and following user IDs
     * @param query the get follow by users query
     * @return the follow relationship if found
     */
    Optional<Follow> handle(GetFollowByUsersQuery query);

    /**
     * Handles the query to get the follower count for a user
     * @param query the get follower count query
     * @return the number of followers
     */
    long handle(GetFollowerCountQuery query);
}
