package com.levelupjourney.microservicecommunity.bounded.infrastructure.persistence.mongodb.repositories;

import com.levelupjourney.microservicecommunity.bounded.domain.model.aggregates.SampleAggregateRoot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository for SampleAggregateRoot following DDD principles.
 * Provides data access methods for the Sample Aggregate.
 */
@Repository
public interface SampleAggregateRepository extends MongoRepository<SampleAggregateRoot, String> {

    /**
     * Find aggregate by business identifier.
     * 
     * @param businessId the business identifier
     * @return Optional containing the aggregate if found
     */
    Optional<SampleAggregateRoot> findByBusinessId(String businessId);

    /**
     * Find aggregates by status using MongoDB query.
     * Example of custom query using @Query annotation.
     * 
     * @param status the status to filter by
     * @return List of aggregates with the specified status
     */
    @Query("{ 'status' : ?0 }")
    List<SampleAggregateRoot> findByStatus(String status);

    /**
     * Check if aggregate exists by business identifier.
     * 
     * @param businessId the business identifier
     * @return true if aggregate exists, false otherwise
     */
    boolean existsByBusinessId(String businessId);

    /**
     * Find aggregates created between dates.
     * Example of query method using Spring Data MongoDB naming conventions.
     * 
     * @param startDate start date
     * @param endDate end date
     * @return List of aggregates created between the dates
     */
    List<SampleAggregateRoot> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find active aggregates.
     * 
     * @return List of active aggregates
     */
    @Query("{ 'status' : 'ACTIVE' }")
    List<SampleAggregateRoot> findActiveAggregates();

    /**
     * Count aggregates by status.
     * 
     * @param status the status to count
     * @return count of aggregates with the specified status
     */
    long countByStatus(String status);
}
