package com.levelup.journey.platform.moderation.domain.model.events;

import com.levelup.journey.platform.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.List;

/**
 * Event emitted when suspicious content is automatically detected
 */
public class SuspiciousContentDetectedEvent implements DomainEvent {
    private final String reportId;
    private final String postId;
    private final String authorId;
    private final List<String> suspiciousWords;
    private final String category;
    private final String severity;
    private final String contentSnippet;
    private final Instant occurredOn;

    public SuspiciousContentDetectedEvent(String reportId, String postId, String authorId,
                                        List<String> suspiciousWords, String category, 
                                        String severity, String contentSnippet) {
        if (reportId == null || reportId.isBlank()) throw new IllegalArgumentException("reportId required");
        if (postId == null || postId.isBlank()) throw new IllegalArgumentException("postId required");
        if (authorId == null || authorId.isBlank()) throw new IllegalArgumentException("authorId required");
        if (suspiciousWords == null || suspiciousWords.isEmpty()) throw new IllegalArgumentException("suspiciousWords required");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("category required");
        if (severity == null || severity.isBlank()) throw new IllegalArgumentException("severity required");
        if (contentSnippet == null || contentSnippet.isBlank()) throw new IllegalArgumentException("contentSnippet required");
        
        this.reportId = reportId;
        this.postId = postId;
        this.authorId = authorId;
        this.suspiciousWords = List.copyOf(suspiciousWords);
        this.category = category;
        this.severity = severity;
        this.contentSnippet = contentSnippet;
        this.occurredOn = Instant.now();
    }

    public String reportId() { return reportId; }
    public String postId() { return postId; }
    public String authorId() { return authorId; }
    public List<String> suspiciousWords() { return suspiciousWords; }
    public String category() { return category; }
    public String severity() { return severity; }
    public String contentSnippet() { return contentSnippet; }
    public Instant occurredOn() { return occurredOn; }
    
    @Override
    public String aggregateId() { return reportId; }
    
    @Override
    public String eventType() { return getClass().getSimpleName(); }
}