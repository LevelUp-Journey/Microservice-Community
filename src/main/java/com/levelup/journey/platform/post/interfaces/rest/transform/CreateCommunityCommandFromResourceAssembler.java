package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.commands.CreateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.interfaces.rest.resources.CreateCommunityResource;

/**
 * Assembler to transform CreateCommunityResource to CreateCommunityCommand
 */
public class CreateCommunityCommandFromResourceAssembler {

    public static CreateCommunityCommand toCommandFromResource(CreateCommunityResource resource) {
        return new CreateCommunityCommand(
                CommunityId.of(resource.id()),
                UserId.of(resource.ownerId()),
                resource.name(),
                resource.description()
        );
    }
}
