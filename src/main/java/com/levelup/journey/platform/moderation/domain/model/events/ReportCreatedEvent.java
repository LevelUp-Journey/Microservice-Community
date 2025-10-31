package com.levelup.journey.platform.moderation.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a new report is created
 */
public class ReportCreatedEvent implements DomainEvent {
    private final String reportId;
    private final String postId;
    private final String category;
    private final String severity;
    private final boolean autoDetected;
    private final Instant occurredOn;

    public ReportCreatedEvent(String reportId, String postId, String category, 
                             String severity, boolean autoDetected) {
        if (reportId == null || reportId.isBlank()) throw new IllegalArgumentException("reportId required");
        if (postId == null || postId.isBlank()) throw new IllegalArgumentException("postId required");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("category required");
        if (severity == null || severity.isBlank()) throw new IllegalArgumentException("severity required");
        
        this.reportId = reportId;
        this.postId = postId;
        this.category = category;
        this.severity = severity;
        this.autoDetected = autoDetected;
        this.occurredOn = Instant.now();
    }

    public String reportId() { return reportId; }
    public String postId() { return postId; }
    public String category() { return category; }
    public String severity() { return severity; }
    public boolean autoDetected() { return autoDetected; }
    public Instant occurredOn() { return occurredOn; }
    
    @Override
    public String aggregateId() { return reportId; }
    
    @Override
    public String eventType() { return getClass().getSimpleName(); }
}