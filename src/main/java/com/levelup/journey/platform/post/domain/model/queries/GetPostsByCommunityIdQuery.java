package com.levelup.journey.platform.post.domain.model.queries;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;

import java.util.Objects;

/**
 * Query to get all posts by community identifier
 */
public record GetPostsByCommunityIdQuery(CommunityId communityId) {
    public GetPostsByCommunityIdQuery {
        Objects.requireNonNull(communityId, "Community ID is required");
    }
}
