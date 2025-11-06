package com.levelup.journey.platform.social.domain.model.commands;

import com.levelup.journey.platform.social.domain.model.valueobjects.ReactionId;

/**
 * Command to remove a reaction
 */
public record RemoveReactionCommand(ReactionId id) {
    public RemoveReactionCommand {
        if (id == null) {
            throw new IllegalArgumentException("Reaction id is required");
        }
    }
}
