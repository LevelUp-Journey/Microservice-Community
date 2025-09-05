package com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Resource representing a sample aggregate in API responses.
 * Contains all relevant information about the aggregate for external consumption.
 */
public record SampleAggregateResource(
    String id,
    String businessId,
    String name,
    String description,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
