package com.levelupjourney.microservicecommunity.bounded.interfaces.rest.transform;

import com.levelupjourney.microservicecommunity.bounded.domain.model.commands.CreateSampleAggregateCommand;
import com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources.CreateSampleAggregateResource;

/**
 * Assembler for transforming CreateSampleAggregateResource to CreateSampleAggregateCommand.
 * Implements the transformation between interface layer and domain layer.
 */
public class CreateSampleAggregateCommandFromResourceAssembler {

    /**
     * Transforms a create resource to a create command.
     * 
     * @param resource the REST resource
     * @return the domain command
     */
    public static CreateSampleAggregateCommand toCommandFromResource(CreateSampleAggregateResource resource) {
        return new CreateSampleAggregateCommand(
            resource.businessId(),
            resource.name(),
            resource.description()
        );
    }
}
