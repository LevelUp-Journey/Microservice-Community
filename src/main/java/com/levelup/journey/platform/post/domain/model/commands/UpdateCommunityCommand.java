package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;

/** Command: Update community */
public record UpdateCommunityCommand(CommunityId communityId, String name, String description, String imageUrl) {
    public UpdateCommunityCommand {
        if (communityId == null) throw new IllegalArgumentException("communityId required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        // description and imageUrl are optional
    }
}
