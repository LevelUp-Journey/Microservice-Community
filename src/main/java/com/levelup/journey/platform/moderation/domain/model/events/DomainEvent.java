package com.levelup.journey.platform.moderation.domain.model.events;

import java.time.Instant;

/**
 * Base domain event for Moderation bounded context
 */
public interface DomainEvent {
    String aggregateId();
    String eventType();
    Instant occurredOn();
}