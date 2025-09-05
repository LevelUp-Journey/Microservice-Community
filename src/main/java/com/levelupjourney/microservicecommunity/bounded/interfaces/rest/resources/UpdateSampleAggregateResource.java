package com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources;

/**
 * Resource for updating a sample aggregate.
 * Represents the request payload for aggregate updates.
 */
public record UpdateSampleAggregateResource(
    String name,
    String description
) {}
