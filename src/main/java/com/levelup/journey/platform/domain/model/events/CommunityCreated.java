package com.levelup.journey.platform.domain.model.events;

import java.time.Instant;

/** Evento emitido cuando se crea una comunidad */
public record CommunityCreated(String aggregateId, String name, String ownerId, Instant occurredOn) implements DomainEvent {
    public CommunityCreated {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId requerido");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name requerido");
        if (ownerId == null || ownerId.isBlank()) throw new IllegalArgumentException("ownerId requerido");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn requerido");
    }
    @Override public String eventType() { return getClass().getSimpleName(); }
}

