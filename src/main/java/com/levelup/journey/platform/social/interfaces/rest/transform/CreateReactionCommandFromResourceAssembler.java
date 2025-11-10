package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.social.domain.model.commands.CreateReactionCommand;
import com.levelup.journey.platform.social.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionType;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.interfaces.rest.resources.CreateReactionResource;

/**
 * Assembler to transform CreateReactionResource to CreateReactionCommand
 */
public class CreateReactionCommandFromResourceAssembler {

    public static CreateReactionCommand toCommandFromResource(CreateReactionResource resource, String userId) {
        return new CreateReactionCommand(
                PostId.of(resource.postId()),
                UserId.of(userId),
                ReactionType.valueOf(resource.reactionType())
        );
    }
}
