package com.levelup.journey.platform.domain.model.commands;

import com.levelup.journey.platform.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

/** Command: Crear comunidad */
public record CreateCommunityCommand(CommunityId id, UserId ownerId, String name, String description) {
    public CreateCommunityCommand {
        if (id == null) throw new IllegalArgumentException("id requerido");
        if (ownerId == null) throw new IllegalArgumentException("ownerId requerido");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name requerido");
        // description opcional
    }
}

