package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.social.domain.model.aggregates.Follow;
import com.levelup.journey.platform.social.interfaces.rest.resources.FollowResource;

/**
 * Assembler to transform Follow entity to FollowResource
 */
public class FollowResourceFromEntityAssembler {

    public static FollowResource toResourceFromEntity(Follow entity) {
        return new FollowResource(
                entity.id().value(),
                entity.followerId().value(),
                entity.followingId().value(),
                entity.createdAt()
        );
    }
}
