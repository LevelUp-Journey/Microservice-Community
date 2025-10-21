package com.levelup.journey.platform.domain.model.events;

import java.time.Instant;

/** Event emitted when a community is created */
public record CommunityCreated(String aggregateId, String name, String ownerId, Instant occurredOn) implements DomainEvent {
    public CommunityCreated {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (ownerId == null || ownerId.isBlank()) throw new IllegalArgumentException("ownerId required");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn required");
    }
    @Override public String eventType() { return getClass().getSimpleName(); }
}

