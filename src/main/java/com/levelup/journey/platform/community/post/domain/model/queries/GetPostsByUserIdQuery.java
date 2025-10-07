package com.levelup.journey.platform.community.post.domain.model.queries;

/**
 * Query to retrieve all posts created by a specific user.
 * Returns only non-deleted posts.
 *
 * @param userId the ID of the user whose posts to retrieve
 */
public record GetPostsByUserIdQuery(Long userId) {
    public GetPostsByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}
