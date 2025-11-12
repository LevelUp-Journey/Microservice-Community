package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.shared.domain.acl.CommunityService;
import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.interfaces.rest.resources.SubscriptionResource;

/**
 * Assembler to transform Subscription entity to SubscriptionResource
 */
public class SubscriptionResourceFromEntityAssembler {

    public static SubscriptionResource toResourceFromEntity(Subscription entity, CommunityService communityService) {
        String communityName = communityService != null ?
            communityService.getCommunityName(entity.communityId().value()) : null;
        String communityImageUrl = communityService != null ?
            communityService.getCommunityImageUrl(entity.communityId().value()) : null;

        return new SubscriptionResource(
                entity.id().value(),
                entity.userId().value(),
                entity.communityId().value(),
                communityName,
                communityImageUrl,
                entity.createdAt()
        );
    }
}
