package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.social.domain.model.commands.CreateReactionCommand;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.interfaces.rest.resources.CreateReactionResource;

/**
 * Assembler to transform CreateReactionResource to CreateReactionCommand
 */
public class CreateReactionCommandFromResourceAssembler {

    public static CreateReactionCommand toCommandFromResource(CreateReactionResource resource) {
        return new CreateReactionCommand(
                ReactionId.of(resource.id()),
                PostId.of(resource.postId()),
                UserId.of(resource.userId()),
                ReactionType.valueOf(resource.reactionType())
        );
    }
}
