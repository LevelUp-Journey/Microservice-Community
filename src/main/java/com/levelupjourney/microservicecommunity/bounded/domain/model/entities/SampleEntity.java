package com.levelupjourney.microservicecommunity.bounded.domain.model.entities;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Sample Entity embedded within an aggregate.
 * In MongoDB DDD, entities within aggregates are typically embedded documents.
 * 
 * This demonstrates:
 * - Embedded entity structure
 * - Business logic within entities
 * - Proper encapsulation
 */
@Getter
public class SampleEntity {

    @Field("entity_id")
    private String entityId;

    @Field("name")
    private String name;

    @Field("type")
    private String type;

    @Field("value")
    private Double value;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("is_active")
    private boolean isActive;

    // Private constructor for frameworks
    protected SampleEntity() {}

    /**
     * Constructor for creating a new entity.
     * 
     * @param entityId unique identifier for the entity
     * @param name name of the entity
     * @param type type of the entity
     * @param value numeric value associated with the entity
     */
    public SampleEntity(String entityId, String name, String type, Double value) {
        if (entityId == null || entityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (value != null && value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }

        this.entityId = entityId;
        this.name = name;
        this.type = type;
        this.value = value;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    /**
     * Business method to update entity details.
     * 
     * @param newName new name for the entity
     * @param newValue new value for the entity
     */
    public void updateDetails(String newName, Double newValue) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (newValue != null && newValue < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }

        this.name = newName;
        this.value = newValue;
    }

    /**
     * Business method to deactivate the entity.
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Business method to activate the entity.
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Business method to check if entity has a specific type.
     * 
     * @param type the type to check
     * @return true if entity has the specified type
     */
    public boolean hasType(String type) {
        return this.type != null && this.type.equals(type);
    }

    /**
     * Business method to calculate some business rule.
     * Example of domain logic within the entity.
     * 
     * @param multiplier the multiplier to apply
     * @return calculated result
     */
    public Double calculateBusinessValue(Double multiplier) {
        if (!isActive || value == null || multiplier == null) {
            return 0.0;
        }
        return value * multiplier;
    }
}
