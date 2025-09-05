package com.levelupjourney.microservicecommunity.posting.domain.model.queries;

import org.springframework.data.domain.Pageable;

/**
 * Query to get a paginated feed of posts.
 */
public record GetPostFeedQuery(
    String authorId, // Optional: filter by author
    Pageable pageable
) {
    
    public GetPostFeedQuery {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
    }
    
    /**
     * Creates a query for all posts.
     */
    public static GetPostFeedQuery allPosts(Pageable pageable) {
        return new GetPostFeedQuery(null, pageable);
    }
    
    /**
     * Creates a query for posts by a specific author.
     */
    public static GetPostFeedQuery byAuthor(String authorId, Pageable pageable) {
        return new GetPostFeedQuery(authorId, pageable);
    }
    
    /**
     * Checks if this query is filtered by author.
     */
    public boolean hasAuthorFilter() {
        return authorId != null && !authorId.trim().isEmpty();
    }
}
