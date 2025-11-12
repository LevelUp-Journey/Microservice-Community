package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.interfaces.rest.resources.PostResource;

/**
 * Assembler to transform Post entity to PostResource
 */
public class PostResourceFromEntityAssembler {

    public static PostResource toResourceFromEntity(Post entity) {
        return new PostResource(
                entity.id().value(),
                entity.communityId().value(),
                entity.authorId().value(),
                entity.authorProfileId().value(),
                entity.content(),
                entity.imageUrl().isEmpty() ? null : entity.imageUrl().url(),
                entity.createdAt()
        );
    }
}
