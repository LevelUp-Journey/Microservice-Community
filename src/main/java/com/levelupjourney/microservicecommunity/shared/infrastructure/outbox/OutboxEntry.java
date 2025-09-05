package com.levelupjourney.microservicecommunity.shared.infrastructure.outbox;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Outbox entry for reliable domain event publishing.
 * Stores domain events that need to be published to external systems.
 */
@Getter
@Document(collection = "outbox")
public class OutboxEntry {
    
    @Id
    private String id;
    
    @Field("event_type")
    @Indexed
    private String eventType;
    
    @Field("aggregate_id")
    @Indexed
    private String aggregateId;
    
    @Field("occurred_at")
    private LocalDateTime occurredAt;
    
    @Field("payload_json")
    private String payloadJson;
    
    @Field("published")
    @Indexed
    private boolean published;
    
    @Field("published_at")
    private LocalDateTime publishedAt;
    
    @Field("retry_count")
    private int retryCount;
    
    @Field("last_retry_at")
    private LocalDateTime lastRetryAt;
    
    // Required by Spring Data MongoDB
    protected OutboxEntry() {}
    
    public OutboxEntry(
        String eventType,
        String aggregateId,
        LocalDateTime occurredAt,
        String payloadJson
    ) {
        this.id = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
        this.payloadJson = payloadJson;
        this.published = false;
        this.retryCount = 0;
    }
    
    /**
     * Marks the event as published.
     */
    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }
    
    /**
     * Increments the retry count.
     */
    public void incrementRetryCount() {
        this.retryCount++;
        this.lastRetryAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the event should be retried.
     */
    public boolean shouldRetry(int maxRetries) {
        return !published && retryCount < maxRetries;
    }
    
    /**
     * Checks if the event is stale (older than specified hours).
     */
    public boolean isStale(int staleAfterHours) {
        return occurredAt.isBefore(LocalDateTime.now().minusHours(staleAfterHours));
    }
}
