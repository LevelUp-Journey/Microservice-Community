package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.social.domain.model.aggregates.Reaction;
import com.levelup.journey.platform.social.interfaces.rest.resources.ReactionResource;

/**
 * Assembler to transform Reaction entity to ReactionResource
 */
public class ReactionResourceFromEntityAssembler {

    public static ReactionResource toResourceFromEntity(Reaction entity) {
        return new ReactionResource(
                entity.id().value(),
                entity.postId().value(),
                entity.userId().value(),
                entity.reactionType().name(),
                entity.createdAt()
        );
    }
}
