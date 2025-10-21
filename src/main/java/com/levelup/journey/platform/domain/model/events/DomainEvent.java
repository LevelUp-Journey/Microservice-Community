package com.levelup.journey.platform.domain.model.events;

import java.time.Instant;

/**
 * Evento de dominio base, usado para comunicación interna entre capas y publicación externa.
 */
public interface DomainEvent {
    String aggregateId();
    String eventType();
    Instant occurredOn();
}

