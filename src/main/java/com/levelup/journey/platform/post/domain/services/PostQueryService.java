package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Post Query Service
 * Defines read operations for posts
 */
public interface PostQueryService {

    /**
     * Handles the query to get a post by its ID
     * @param query the get post by id query
     * @return the post if found
     */
    Optional<Post> handle(GetPostByIdQuery query);

    /**
     * Handles the query to get all posts
     * @param query the get all posts query
     * @return list of all posts
     */
    List<Post> handle(GetAllPostsQuery query);

    /**
     * Handles the query to get posts by community ID
     * @param query the get posts by community id query
     * @return list of posts in the community
     */
    List<Post> handle(GetPostsByCommunityIdQuery query);
}
