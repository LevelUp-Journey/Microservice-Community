package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.interfaces.rest.resources.CommunityResource;

/**
 * Assembler to transform Community entity to CommunityResource
 */
public class CommunityResourceFromEntityAssembler {

    public static CommunityResource toResourceFromEntity(Community entity) {
        return new CommunityResource(
                entity.id().value(),
                entity.ownerId().value(),
                entity.ownerProfileId().value(),
                entity.name(),
                entity.description(),
                entity.imageUrl().isEmpty() ? null : entity.imageUrl().url(),
                entity.createdAt()
        );
    }
}
