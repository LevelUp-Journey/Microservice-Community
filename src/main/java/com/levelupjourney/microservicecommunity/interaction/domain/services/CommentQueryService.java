package com.levelupjourney.microservicecommunity.interaction.domain.services;

import com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for comment query operations.
 */
public interface CommentQueryService {
    
    /**
     * Retrieves a comment by its ID.
     * 
     * @param commentId the comment ID
     * @return the comment if found
     */
    Optional<Comment> findById(String commentId);
    
    /**
     * Retrieves all comments for a post.
     * 
     * @param postId the post ID
     * @return list of comments for the post
     */
    List<Comment> findByPostId(String postId);
}
