package com.levelup.journey.platform.post.domain.model.queries;

import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/** Query: Obtener comunidades por ID del usuario creador */
public record GetCommunitiesByCreatorUserIdQuery(UserId creatorUserId) {
    public GetCommunitiesByCreatorUserIdQuery {
        if (creatorUserId == null) throw new IllegalArgumentException("creatorUserId requerido");
    }
}
