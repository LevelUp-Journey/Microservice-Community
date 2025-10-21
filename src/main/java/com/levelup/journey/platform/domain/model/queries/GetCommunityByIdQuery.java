package com.levelup.journey.platform.domain.model.queries;

import com.levelup.journey.platform.domain.model.valueobjects.CommunityId;

/** Query: Obtener comunidad por id */
public record GetCommunityByIdQuery(CommunityId id) {
    public GetCommunityByIdQuery {
        if (id == null) throw new IllegalArgumentException("id requerido");
    }
}

