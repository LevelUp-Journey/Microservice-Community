package com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources;

/**
 * Resource for creating a new sample aggregate.
 * Represents the request payload for aggregate creation.
 */
public record CreateSampleAggregateResource(
    String businessId,
    String name,
    String description
) {}
