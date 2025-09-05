package com.levelupjourney.microservicecommunity.bounded.domain.model.commands;

/**
 * Sample command for creating a new aggregate.
 * Commands represent the intent to change the system state.
 * 
 * This demonstrates:
 * - Immutable command structure
 * - Clear business intent
 * - Validation within commands
 */
public record CreateSampleAggregateCommand(
    String businessId,
    String name,
    String description
) {
    
    /**
     * Constructor with validation.
     */
    public CreateSampleAggregateCommand {
        if (businessId == null || businessId.trim().isEmpty()) {
            throw new IllegalArgumentException("Business ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}
