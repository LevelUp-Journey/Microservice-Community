package com.levelupjourney.microservicecommunity.shared.domain.model.aggregates;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Abstract class for aggregate roots that need auditing capabilities.
 * It extends AbstractAggregateRoot to support domain events.
 * MongoDB version using Spring Data MongoDB annotations.
 *
 * @param <T> the type of the aggregate root
 */
@Getter
public abstract class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {
    
    @Id
    private String id;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Registers a domain event to be published.
     *
     * @param event the domain event to register
     */
    public void addDomainEvent(Object event) {
        registerEvent(event);
    }
}
