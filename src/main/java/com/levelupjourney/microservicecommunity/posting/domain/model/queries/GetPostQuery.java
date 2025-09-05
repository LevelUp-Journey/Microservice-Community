package com.levelupjourney.microservicecommunity.posting.domain.model.queries;

/**
 * Query to get a post by its ID.
 */
public record GetPostQuery(
    String postId
) {
    
    public GetPostQuery {
        if (postId == null || postId.trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }
    }
}
