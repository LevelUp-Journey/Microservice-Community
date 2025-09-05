package com.levelupjourney.microservicecommunity.posting.domain.services;

import com.levelupjourney.microservicecommunity.posting.domain.model.aggregates.Post;
import com.levelupjourney.microservicecommunity.posting.domain.model.queries.GetPostQuery;
import com.levelupjourney.microservicecommunity.posting.domain.model.queries.GetPostFeedQuery;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * Domain service interface for post query operations.
 * Follows CQRS pattern by separating query operations.
 */
public interface PostQueryService {
    
    /**
     * Handles getting a single post by ID.
     * 
     * @param query the get post query
     * @return the post if found
     */
    Optional<Post> handle(GetPostQuery query);
    
    /**
     * Handles getting a paginated feed of posts.
     * 
     * @param query the get post feed query
     * @return a page of posts
     */
    Page<Post> handle(GetPostFeedQuery query);
}
