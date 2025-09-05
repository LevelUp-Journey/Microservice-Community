package com.levelupjourney.microservicecommunity.shared.domain.model.events;

import java.time.LocalDateTime;

/**
 * Base interface for all domain events in the Community microservice.
 * Domain events represent something that has happened in the domain.
 */
public interface DomainEvent {
    
    /**
     * Gets the timestamp when the event occurred.
     * 
     * @return the occurrence timestamp
     */
    LocalDateTime occurredAt();
    
    /**
     * Gets the name of the event type.
     * 
     * @return the event type name
     */
    String eventType();
    
    /**
     * Gets the aggregate ID that generated this event.
     * 
     * @return the aggregate identifier
     */
    String aggregateId();
}
