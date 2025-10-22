package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.social.domain.model.commands.CreateFollowCommand;
import com.levelup.journey.platform.social.domain.model.valueobjects.FollowId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.interfaces.rest.resources.CreateFollowResource;

/**
 * Assembler to transform CreateFollowResource to CreateFollowCommand
 */
public class CreateFollowCommandFromResourceAssembler {

    public static CreateFollowCommand toCommandFromResource(CreateFollowResource resource) {
        return new CreateFollowCommand(
                FollowId.of(resource.id()),
                UserId.of(resource.followerId()),
                UserId.of(resource.followingId())
        );
    }
}
