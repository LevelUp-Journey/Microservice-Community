package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.commands.PublishPostCommand;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.interfaces.rest.resources.CreatePostResource;

/**
 * Assembler to transform CreatePostResource to PublishPostCommand
 */
public class CreatePostCommandFromResourceAssembler {

    public static PublishPostCommand toCommandFromResource(CreatePostResource resource, String authorId) {
        String imageUrl = resource.imageUrl() != null ? resource.imageUrl().toString() : null;
        return new PublishPostCommand(
                CommunityId.of(resource.communityId()),
                UserId.of(authorId),
                resource.content(),
                imageUrl
        );
    }
}
