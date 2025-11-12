package com.levelup.journey.platform.post.interfaces.rest.transform;

import com.levelup.journey.platform.post.domain.model.aggregates.Community;
import com.levelup.journey.platform.post.interfaces.rest.resources.CommunityResource;
import com.levelup.journey.platform.shared.domain.acl.SocialRelationshipService;
import org.springframework.stereotype.Component;

/**
 * Assembler to transform Community entity to CommunityResource
 */
@Component
public class CommunityResourceFromEntityAssembler {

    private final SocialRelationshipService socialRelationshipService;

    public CommunityResourceFromEntityAssembler(SocialRelationshipService socialRelationshipService) {
        this.socialRelationshipService = socialRelationshipService;
    }

    public CommunityResource toResourceFromEntity(Community entity) {
        int followerCount = socialRelationshipService.getSubscriberCountByCommunityId(entity.id().value());

        return new CommunityResource(
                entity.id().value(),
                entity.ownerId().value(),
                entity.ownerProfileId().value(),
                entity.name(),
                entity.description(),
                entity.imageUrl().isEmpty() ? null : entity.imageUrl().url(),
                entity.createdAt(),
                followerCount
        );
    }
}
