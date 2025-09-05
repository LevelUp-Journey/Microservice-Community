package com.levelupjourney.microservicecommunity.bounded.application.internal.commandservices;

import com.levelupjourney.microservicecommunity.bounded.domain.model.aggregates.SampleAggregateRoot;
import com.levelupjourney.microservicecommunity.bounded.domain.model.commands.CreateSampleAggregateCommand;
import com.levelupjourney.microservicecommunity.bounded.infrastructure.persistence.mongodb.repositories.SampleAggregateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Command service for handling Sample Aggregate commands.
 * Implements the application layer of DDD, orchestrating domain operations.
 * 
 * This demonstrates:
 * - Command handling patterns
 * - Repository usage
 * - Business logic orchestration
 * - Transaction boundaries
 */
@Service
public class SampleAggregateCommandService {

    private final SampleAggregateRepository sampleAggregateRepository;

    public SampleAggregateCommandService(SampleAggregateRepository sampleAggregateRepository) {
        this.sampleAggregateRepository = sampleAggregateRepository;
    }

    /**
     * Handles the creation of a new sample aggregate.
     * 
     * @param command the create command
     * @return the ID of the created aggregate
     * @throws IllegalArgumentException if business rules are violated
     */
    public String handle(CreateSampleAggregateCommand command) {
        // Check business rules - aggregate with same business ID should not exist
        if (sampleAggregateRepository.existsByBusinessId(command.businessId())) {
            throw new IllegalArgumentException("Aggregate with business ID " + command.businessId() + " already exists");
        }

        // Create new aggregate using domain logic
        SampleAggregateRoot aggregate = new SampleAggregateRoot(
            command.businessId(),
            command.name(),
            command.description()
        );

        // Save aggregate (this will also publish domain events)
        SampleAggregateRoot savedAggregate = sampleAggregateRepository.save(aggregate);

        return savedAggregate.getId();
    }

    /**
     * Handles updating an existing aggregate.
     * 
     * @param businessId the business identifier
     * @param newName the new name
     * @param newDescription the new description
     * @throws IllegalArgumentException if aggregate not found
     */
    public void updateAggregate(String businessId, String newName, String newDescription) {
        // Retrieve aggregate
        Optional<SampleAggregateRoot> optionalAggregate = sampleAggregateRepository.findByBusinessId(businessId);
        
        if (optionalAggregate.isEmpty()) {
            throw new IllegalArgumentException("Aggregate with business ID " + businessId + " not found");
        }

        SampleAggregateRoot aggregate = optionalAggregate.get();

        // Use domain method to update (maintains business invariants)
        aggregate.updateDetails(newName, newDescription);

        // Save updated aggregate
        sampleAggregateRepository.save(aggregate);
    }

    /**
     * Handles deactivating an aggregate.
     * 
     * @param businessId the business identifier
     * @throws IllegalArgumentException if aggregate not found
     */
    public void deactivateAggregate(String businessId) {
        Optional<SampleAggregateRoot> optionalAggregate = sampleAggregateRepository.findByBusinessId(businessId);
        
        if (optionalAggregate.isEmpty()) {
            throw new IllegalArgumentException("Aggregate with business ID " + businessId + " not found");
        }

        SampleAggregateRoot aggregate = optionalAggregate.get();

        // Use domain method to deactivate
        aggregate.deactivate();

        // Save updated aggregate
        sampleAggregateRepository.save(aggregate);
    }
}
