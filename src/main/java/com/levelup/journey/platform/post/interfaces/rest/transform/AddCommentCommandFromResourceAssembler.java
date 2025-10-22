package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.interfaces.rest.resources.AddCommentResource;

/**
 * Assembler to transform AddCommentResource to AddCommentCommand
 */
public class AddCommentCommandFromResourceAssembler {

    public static AddCommentCommand toCommandFromResource(String postId, AddCommentResource resource) {
        return new AddCommentCommand(
                PostId.of(postId),
                CommentId.of(resource.commentId()),
                UserId.of(resource.authorId()),
                resource.content()
        );
    }
}
