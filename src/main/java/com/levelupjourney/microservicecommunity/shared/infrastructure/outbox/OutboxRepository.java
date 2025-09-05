package com.levelupjourney.microservicecommunity.shared.infrastructure.outbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing outbox entries.
 */
@Repository
public interface OutboxRepository extends MongoRepository<OutboxEntry, String> {
    
    /**
     * Finds all unpublished entries ordered by occurrence time.
     */
    List<OutboxEntry> findByPublishedFalseOrderByOccurredAtAsc();
    
    /**
     * Finds unpublished entries with pagination.
     */
    Page<OutboxEntry> findByPublishedFalse(Pageable pageable);
    
    /**
     * Finds entries that need retry (unpublished and under retry limit).
     */
    @Query("{ 'published': false, 'retryCount': { $lt: ?0 } }")
    List<OutboxEntry> findRetryableEntries(int maxRetries);
    
    /**
     * Finds stale entries that occurred before the specified time.
     */
    @Query("{ 'published': false, 'occurredAt': { $lt: ?0 } }")
    List<OutboxEntry> findStaleEntries(LocalDateTime before);
    
    /**
     * Counts unpublished entries.
     */
    long countByPublishedFalse();
    
    /**
     * Finds entries by event type.
     */
    List<OutboxEntry> findByEventTypeOrderByOccurredAtAsc(String eventType);
    
    /**
     * Finds entries by aggregate ID.
     */
    List<OutboxEntry> findByAggregateIdOrderByOccurredAtAsc(String aggregateId);
}
