package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.interfaces.rest.resources.AddCommentResource;

/**
 * Assembler to transform AddCommentResource to AddCommentCommand
 */
public class AddCommentCommandFromResourceAssembler {

    public static AddCommentCommand toCommandFromResource(String postId, AddCommentResource resource) {
        String imageUrl = resource.imageUrl() != null ? resource.imageUrl().toString() : null;
        return new AddCommentCommand(
                PostId.of(postId),
                UserId.of(resource.authorId()),
                resource.content(),
                imageUrl
        );
    }
}
