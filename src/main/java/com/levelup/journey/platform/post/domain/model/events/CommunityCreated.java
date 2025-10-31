package com.levelup.journey.platform.post.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;

/** Event emitted when a community is created */
public class CommunityCreated implements DomainEvent {
    private final String aggregateId;
    private final String name;
    private final String ownerId;
    private final Instant occurredOn;

    public CommunityCreated(String aggregateId, String name, String ownerId, Instant occurredOn) {
        this.aggregateId = aggregateId;
        this.name = name;
        this.ownerId = ownerId;
        this.occurredOn = occurredOn;

        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (ownerId == null || ownerId.isBlank()) throw new IllegalArgumentException("ownerId required");
        if (occurredOn == null) throw new IllegalArgumentException("occurredOn required");
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public String eventType() {
        return getClass().getSimpleName();
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public String name() {
        return name;
    }

    public String ownerId() {
        return ownerId;
    }
}

