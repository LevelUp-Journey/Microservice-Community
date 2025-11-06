package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.commands.UpdateCommunityCommand;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.interfaces.rest.resources.UpdateCommunityResource;

/**
 * Assembler to transform UpdateCommunityResource to UpdateCommunityCommand
 */
public class UpdateCommunityCommandFromResourceAssembler {

    public static UpdateCommunityCommand toCommandFromResource(String communityId, UpdateCommunityResource resource) {
        String imageUrl = resource.imageUrl() != null ? resource.imageUrl().toString() : null;
        return new UpdateCommunityCommand(
                CommunityId.of(communityId),
                resource.name(),
                resource.description(),
                imageUrl
        );
    }
}
