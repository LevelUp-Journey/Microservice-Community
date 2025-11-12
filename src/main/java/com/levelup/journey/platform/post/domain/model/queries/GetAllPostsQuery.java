package com.levelup.journey.platform.post.domain.model.queries;

/**
 * Query to get all posts with pagination
 */
public record GetAllPostsQuery(int page, int size) {
    public GetAllPostsQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }
}
