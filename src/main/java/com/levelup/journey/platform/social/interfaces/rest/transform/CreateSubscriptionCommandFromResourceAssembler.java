package com.levelup.journey.platform.social.interfaces.rest.transform;

import com.levelup.journey.platform.social.domain.model.commands.CreateSubscriptionCommand;
import com.levelup.journey.platform.social.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.social.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.social.interfaces.rest.resources.CreateSubscriptionResource;

/**
 * Assembler to transform CreateSubscriptionResource to CreateSubscriptionCommand
 */
public class CreateSubscriptionCommandFromResourceAssembler {

    public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionResource resource, String userId) {
        return new CreateSubscriptionCommand(
                UserId.of(userId),
                CommunityId.of(resource.communityId())
        );
    }
}
