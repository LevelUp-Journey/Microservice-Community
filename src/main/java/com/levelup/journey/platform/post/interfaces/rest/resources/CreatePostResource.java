package com.levelup.journey.platform.post.interfaces.rest.resources;

/**
 * Create Post Resource
 * Request payload for creating a new post
 */
public record CreatePostResource(
        String id,
        String communityId,
        String authorId,
        String title,
        String content
) {
}
