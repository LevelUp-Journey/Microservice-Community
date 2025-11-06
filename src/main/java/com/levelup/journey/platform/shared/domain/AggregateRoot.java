package com.levelup.journey.platform.shared.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for aggregates that accumulate domain events.
 * Aggregates are consistency boundaries that encapsulate business rules
 * and maintain invariants across multiple domain objects.
 */
public abstract class AggregateRoot {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Records a domain event for later publication.
     * @param event The domain event to record
     */
    protected void recordEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * Pulls all recorded domain events and clears the list.
     * @return List of domain events that were recorded
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    /**
     * Returns a read-only view of currently recorded domain events.
     * @return Unmodifiable list of domain events
     */
    public List<DomainEvent> peekDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}