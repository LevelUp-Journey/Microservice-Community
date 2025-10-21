package com.levelup.journey.platform.domain.model.events;

import java.time.Instant;

/**
 * Base domain event, used for internal communication between layers and external publication.
 */
public interface DomainEvent {
    String aggregateId();
    String eventType();
    Instant occurredOn();
}

