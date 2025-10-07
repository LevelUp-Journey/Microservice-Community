package com.levelup.journey.platform.community.post.domain.model.queries;

/**
 * Query to retrieve a post by its ID.
 * Returns only non-deleted posts.
 *
 * @param postId the ID of the post to retrieve
 */
public record GetPostByIdQuery(Long postId) {
    public GetPostByIdQuery {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Post ID must be a positive number");
        }
    }
}
