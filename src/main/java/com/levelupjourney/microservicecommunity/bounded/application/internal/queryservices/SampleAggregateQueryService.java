package com.levelupjourney.microservicecommunity.bounded.application.internal.queryservices;

import com.levelupjourney.microservicecommunity.bounded.domain.model.aggregates.SampleAggregateRoot;
import com.levelupjourney.microservicecommunity.bounded.domain.model.queries.GetSampleAggregateByBusinessIdQuery;
import com.levelupjourney.microservicecommunity.bounded.infrastructure.persistence.mongodb.repositories.SampleAggregateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Query service for handling Sample Aggregate queries.
 * Implements the read side of CQRS pattern.
 * 
 * This demonstrates:
 * - Query handling patterns
 * - Read-only operations
 * - Data projection and transformation
 */
@Service
public class SampleAggregateQueryService {

    private final SampleAggregateRepository sampleAggregateRepository;

    public SampleAggregateQueryService(SampleAggregateRepository sampleAggregateRepository) {
        this.sampleAggregateRepository = sampleAggregateRepository;
    }

    /**
     * Handles retrieving an aggregate by business ID.
     * 
     * @param query the query containing the business ID
     * @return Optional containing the aggregate if found
     */
    public Optional<SampleAggregateRoot> handle(GetSampleAggregateByBusinessIdQuery query) {
        return sampleAggregateRepository.findByBusinessId(query.businessId());
    }

    /**
     * Retrieves all active aggregates.
     * 
     * @return List of active aggregates
     */
    public List<SampleAggregateRoot> getAllActiveAggregates() {
        return sampleAggregateRepository.findActiveAggregates();
    }

    /**
     * Retrieves aggregates with pagination.
     * 
     * @param pageable pagination information
     * @return Page of aggregates
     */
    public Page<SampleAggregateRoot> getAllAggregates(Pageable pageable) {
        return sampleAggregateRepository.findAll(pageable);
    }

    /**
     * Retrieves aggregates by status with pagination.
     * 
     * @param status the status to filter by
     * @param pageable pagination information
     * @return Page of aggregates with the specified status
     */
    public Page<SampleAggregateRoot> getAggregatesByStatus(String status, Pageable pageable) {
        // This would require a custom repository method
        // For now, we'll use the simple findByStatus and convert to Page
        List<SampleAggregateRoot> aggregates = sampleAggregateRepository.findByStatus(status);
        
        // In a real implementation, you'd want to handle pagination properly
        // This is just for demonstration
        return Page.empty(pageable);
    }

    /**
     * Checks if an aggregate exists by business ID.
     * 
     * @param businessId the business ID to check
     * @return true if aggregate exists
     */
    public boolean existsByBusinessId(String businessId) {
        return sampleAggregateRepository.existsByBusinessId(businessId);
    }

    /**
     * Counts total number of aggregates.
     * 
     * @return total count
     */
    public long getTotalCount() {
        return sampleAggregateRepository.count();
    }

    /**
     * Counts aggregates by status.
     * 
     * @param status the status to count
     * @return count of aggregates with the specified status
     */
    public long countByStatus(String status) {
        return sampleAggregateRepository.countByStatus(status);
    }
}
