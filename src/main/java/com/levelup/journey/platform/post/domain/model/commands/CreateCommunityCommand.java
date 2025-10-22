package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/** Command: Create community */
public record CreateCommunityCommand(UserId ownerId, String name, String description, String imageUrl) {
    public CreateCommunityCommand {
        if (ownerId == null) throw new IllegalArgumentException("ownerId required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        // description and imageUrl are optional
    }
}

