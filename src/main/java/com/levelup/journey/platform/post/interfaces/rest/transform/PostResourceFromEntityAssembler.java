package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.interfaces.rest.resources.CommentResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.PostResource;

import java.util.stream.Collectors;

/**
 * Assembler to transform Post entity to PostResource
 */
public class PostResourceFromEntityAssembler {

    public static PostResource toResourceFromEntity(Post entity) {
        var comments = entity.comments().stream()
                .map(comment -> new CommentResource(
                        comment.id().value(),
                        comment.authorId().value(),
                        comment.content(),
                        comment.imageUrl().isEmpty() ? null : comment.imageUrl().url(),
                        comment.createdAt()
                ))
                .collect(Collectors.toList());

        return new PostResource(
                entity.id().value(),
                entity.communityId().value(),
                entity.authorId().value(),
                entity.title(),
                entity.content(),
                entity.imageUrl().isEmpty() ? null : entity.imageUrl().url(),
                entity.createdAt(),
                comments
        );
    }
}
