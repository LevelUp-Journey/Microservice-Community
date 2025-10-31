package com.levelup.journey.platform.shared.domain;

import java.time.Instant;

/**
 * Base domain event interface for all bounded contexts.
 * Domain events represent significant business events that have occurred.
 */
public interface DomainEvent {
    /**
     * Returns the aggregate ID that this event relates to.
     */
    String aggregateId();

    /**
     * Returns the type of the event.
     */
    String eventType();

    /**
     * Returns when the event occurred.
     */
    Instant occurredOn();
}