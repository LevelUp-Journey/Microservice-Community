package com.levelupjourney.microservicecommunity.bounded.domain.model.aggregates;

import com.levelupjourney.microservicecommunity.bounded.domain.model.entities.SampleEntity;
import com.levelupjourney.microservicecommunity.bounded.domain.model.valueobjects.SampleValueObject;
import com.levelupjourney.microservicecommunity.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Sample Aggregate Root for demonstrating MongoDB DDD structure.
 * Replace with your actual business aggregate.
 * 
 * This aggregate demonstrates:
 * - MongoDB document mapping
 * - Embedded entities and value objects
 * - Business methods and invariants
 * - Domain events
 */
@Getter
@Document(collection = "sample_aggregates")
public class SampleAggregateRoot extends AuditableAbstractAggregateRoot<SampleAggregateRoot> {

    @Indexed(unique = true)
    @Field("business_id")
    private String businessId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("status")
    private String status;

    // Embedded entities within the aggregate
    @Field("sample_entities")
    private List<SampleEntity> sampleEntities;

    // Embedded value object
    @Field("sample_value_object")
    private SampleValueObject sampleValueObject;

    // Private constructor for frameworks
    protected SampleAggregateRoot() {}

    /**
     * Constructor for creating a new aggregate.
     * 
     * @param businessId unique business identifier
     * @param name the name of the aggregate
     * @param description description of the aggregate
     */
    public SampleAggregateRoot(String businessId, String name, String description) {
        this.businessId = businessId;
        this.name = name;
        this.description = description;
        this.status = "ACTIVE";
        this.sampleEntities = new java.util.ArrayList<>();
        
        // Register domain event
        // addDomainEvent(new SampleAggregateCreatedEvent(this.businessId));
    }

    /**
     * Business method to update the aggregate.
     * Demonstrates encapsulation of business logic.
     * 
     * @param newName new name for the aggregate
     * @param newDescription new description for the aggregate
     */
    public void updateDetails(String newName, String newDescription) {
        // Business validation
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        this.name = newName;
        this.description = newDescription;
        
        // Register domain event
        // addDomainEvent(new SampleAggregateUpdatedEvent(this.businessId));
    }

    /**
     * Business method to add an entity to the aggregate.
     * 
     * @param entity the entity to add
     */
    public void addSampleEntity(SampleEntity entity) {
        // Business validation and invariants
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        // Initialize list if null
        if (this.sampleEntities == null) {
            this.sampleEntities = new java.util.ArrayList<>();
        }
        
        if (this.sampleEntities.size() >= 10) {
            throw new IllegalStateException("Cannot add more than 10 entities");
        }
        
        this.sampleEntities.add(entity);
        
        // Register domain event
        // addDomainEvent(new SampleEntityAddedEvent(this.businessId, entity.getId()));
    }

    /**
     * Business method to deactivate the aggregate.
     */
    public void deactivate() {
        if ("INACTIVE".equals(this.status)) {
            throw new IllegalStateException("Aggregate is already inactive");
        }
        
        this.status = "INACTIVE";
        
        // Register domain event
        // addDomainEvent(new SampleAggregateDeactivatedEvent(this.businessId));
    }

    /**
     * Business method to check if aggregate is active.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}
