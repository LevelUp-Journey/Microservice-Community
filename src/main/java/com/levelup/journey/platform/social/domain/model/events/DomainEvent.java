package com.levelup.journey.platform.social.domain.model.events;

import java.time.Instant;

/**
 * Base domain event for Social bounded context
 */
public interface DomainEvent {
    String aggregateId();
    String eventType();
    Instant occurredOn();
}
