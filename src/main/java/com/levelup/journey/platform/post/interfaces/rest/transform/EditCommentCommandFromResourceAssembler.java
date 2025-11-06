package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.commands.EditCommentCommand;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.interfaces.rest.resources.EditCommentResource;

/**
 * Assembler to transform EditCommentResource to EditCommentCommand
 */
public class EditCommentCommandFromResourceAssembler {

    public static EditCommentCommand toCommandFromResource(String postId, String commentId, String requesterId, EditCommentResource resource) {
        String imageUrl = resource.imageUrl() != null ? resource.imageUrl().toString() : null;
        return new EditCommentCommand(
                PostId.of(postId),
                CommentId.of(commentId),
                UserId.of(requesterId),
                resource.content(),
                imageUrl
        );
    }
}