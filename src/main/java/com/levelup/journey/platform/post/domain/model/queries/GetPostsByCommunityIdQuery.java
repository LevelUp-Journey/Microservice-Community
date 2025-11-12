package com.levelup.journey.platform.post.domain.model.queries;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;

import java.util.Objects;

/**
 * Query to get all posts by community identifier with pagination
 */
public record GetPostsByCommunityIdQuery(CommunityId communityId, int page, int size) {
    public GetPostsByCommunityIdQuery {
        Objects.requireNonNull(communityId, "Community ID is required");
        if (page < 0) {
            throw new IllegalArgumentException("Page must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }
}
