package com.levelup.journey.platform.domain.model.commands;

import com.levelup.journey.platform.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

/** Command: Create community */
public record CreateCommunityCommand(CommunityId id, UserId ownerId, String name, String description) {
    public CreateCommunityCommand {
        if (id == null) throw new IllegalArgumentException("id required");
        if (ownerId == null) throw new IllegalArgumentException("ownerId required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        // description optional
    }
}

