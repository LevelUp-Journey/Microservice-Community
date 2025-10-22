package com.levelup.journey.platform.post.interfaces.rest.resources;

/**
 * Create Community Resource
 * Request payload for creating a new community
 */
public record CreateCommunityResource(
        String id,
        String ownerId,
        String name,
        String description
) {
}
