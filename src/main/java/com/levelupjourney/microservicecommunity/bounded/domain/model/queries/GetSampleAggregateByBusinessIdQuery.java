package com.levelupjourney.microservicecommunity.bounded.domain.model.queries;

/**
 * Sample query for retrieving aggregate information.
 * Queries represent the intent to read system state.
 * 
 * This demonstrates:
 * - Immutable query structure
 * - Clear read intent
 * - Query parameters
 */
public record GetSampleAggregateByBusinessIdQuery(
    String businessId
) {
    
    /**
     * Constructor with validation.
     */
    public GetSampleAggregateByBusinessIdQuery {
        if (businessId == null || businessId.trim().isEmpty()) {
            throw new IllegalArgumentException("Business ID cannot be empty");
        }
    }
}
