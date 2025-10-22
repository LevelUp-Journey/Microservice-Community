package com.levelup.journey.platform.post.interfaces.rest.resources;

/**
 * Add Comment Resource
 * Request payload for adding a comment to a post
 */
public record AddCommentResource(
        String commentId,
        String authorId,
        String content
) {
}
