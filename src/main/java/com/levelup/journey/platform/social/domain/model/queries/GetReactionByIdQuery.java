package com.levelup.journey.platform.social.domain.model.queries;

import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;

/**
 * Query to get a reaction by its ID
 */
public record GetReactionByIdQuery(ReactionId id) {
    public GetReactionByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Reaction id is required");
        }
    }
}
