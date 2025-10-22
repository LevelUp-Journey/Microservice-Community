package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.social.domain.model.aggregates.Subscription;
import com.levelup.journey.platform.social.interfaces.rest.resources.SubscriptionResource;

/**
 * Assembler to transform Subscription entity to SubscriptionResource
 */
public class SubscriptionResourceFromEntityAssembler {

    public static SubscriptionResource toResourceFromEntity(Subscription entity) {
        return new SubscriptionResource(
                entity.id().value(),
                entity.userId().value(),
                entity.communityId().value(),
                entity.createdAt()
        );
    }
}
