package com.levelupjourney.microservicecommunity.bounded.interfaces.rest.transform;

import com.levelupjourney.microservicecommunity.bounded.domain.model.aggregates.SampleAggregateRoot;
import com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources.SampleAggregateResource;

/**
 * Assembler for transforming SampleAggregateRoot to SampleAggregateResource.
 * Implements the transformation between domain layer and interface layer.
 */
public class SampleAggregateResourceFromEntityAssembler {

    /**
     * Transforms a domain aggregate to a REST resource.
     * 
     * @param entity the domain aggregate
     * @return the REST resource
     */
    public static SampleAggregateResource toResourceFromEntity(SampleAggregateRoot entity) {
        return new SampleAggregateResource(
            entity.getId(),
            entity.getBusinessId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
