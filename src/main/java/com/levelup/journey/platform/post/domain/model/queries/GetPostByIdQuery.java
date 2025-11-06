package com.levelup.journey.platform.post.domain.model.queries;

import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;

import java.util.Objects;

/**
 * Query to get a post by its identifier
 */
public record GetPostByIdQuery(PostId id) {
    public GetPostByIdQuery {
        Objects.requireNonNull(id, "Post ID is required");
    }
}
