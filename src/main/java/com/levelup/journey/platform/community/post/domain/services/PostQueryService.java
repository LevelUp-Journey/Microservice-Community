package com.levelup.journey.platform.community.post.domain.services;

import com.levelup.journey.platform.community.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.community.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.community.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.community.post.domain.model.queries.GetPostsByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for Post query operations.
 * Handles all read-only operations for posts.
 */
public interface PostQueryService {

    /**
     * Retrieves a post by its ID.
     * Returns only non-deleted posts.
     *
     * @param query the query containing the post ID
     * @return Optional containing the Post if found, empty otherwise
     */
    Optional<Post> handle(GetPostByIdQuery query);

    /**
     * Retrieves all non-deleted posts.
     * Posts are ordered by creation date (newest first).
     *
     * @param query the get all posts query
     * @return List of all non-deleted posts
     */
    List<Post> handle(GetAllPostsQuery query);

    /**
     * Retrieves all posts created by a specific user.
     * Returns only non-deleted posts.
     *
     * @param query the query containing the user ID
     * @return List of posts created by the user
     */
    List<Post> handle(GetPostsByUserIdQuery query);
}
