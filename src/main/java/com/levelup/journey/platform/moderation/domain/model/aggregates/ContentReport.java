package com.levelup.journey.platform.moderation.domain.model.aggregates;

import com.levelup.journey.platform.moderation.domain.model.entities.ReportAction;
import com.levelup.journey.platform.moderation.domain.model.events.ReportCreatedEvent;
import com.levelup.journey.platform.moderation.domain.model.events.SuspiciousContentDetectedEvent;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ContentReport Aggregate Root
 * Manages content reports and moderation actions
 */
public final class ContentReport extends AggregateRoot {
    private final ReportId id;
    private final PostId postId;
    private final UserId reporterUserId;
    private final UserId reportedUserId;
    private final ReportCategory category;
    private final ReportSeverity severity;
    private final String description;
    private ReportStatus status;
    private final boolean autoDetected;
    private final List<String> suspiciousWords;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<ReportAction> actions = new ArrayList<>();

    private ContentReport(ReportId id, PostId postId, UserId reporterUserId, 
                         UserId reportedUserId, ReportCategory category, 
                         ReportSeverity severity, String description, ReportStatus status,
                         boolean autoDetected, List<String> suspiciousWords, 
                         LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "id required");
        this.postId = Objects.requireNonNull(postId, "postId required");
        this.reporterUserId = reporterUserId; // Can be null for auto-detected reports
        this.reportedUserId = Objects.requireNonNull(reportedUserId, "reportedUserId required");
        this.category = Objects.requireNonNull(category, "category required");
        this.severity = Objects.requireNonNull(severity, "severity required");
        this.description = requireText(description, "description");
        this.status = Objects.requireNonNullElse(status, ReportStatus.PENDING);
        this.autoDetected = autoDetected;
        this.suspiciousWords = suspiciousWords != null ? new ArrayList<>(suspiciousWords) : new ArrayList<>();
        this.createdAt = Objects.requireNonNullElse(createdAt, LocalDateTime.now());
        this.updatedAt = this.createdAt;
    }

    /**
     * Create a new manual report
     */
    public static ContentReport create(ReportId id, PostId postId, UserId reporterUserId,
                                     UserId reportedUserId, ReportCategory category,
                                     String description) {
        ReportSeverity severity = determineSeverity(category);
        
        ContentReport report = new ContentReport(
            id, postId, reporterUserId, reportedUserId, 
            category, severity, description, ReportStatus.PENDING,
            false, new ArrayList<>(), LocalDateTime.now()
        );
        
        // Add initial action
        report.addAction(ReportAction.create(
            ActionType.REVIEWED, 
            reporterUserId, 
            "Manual report created by user"
        ));
        
        // Emit event
        report.recordEvent(new ReportCreatedEvent(
            id.value().toString(),
            postId.value().toString(),
            category.name(),
            severity.name(),
            false
        ));
        
        return report;
    }

    /**
     * Create an auto-detected report
     */
    public static ContentReport createAutoDetected(ReportId id, PostId postId, 
                                                 UserId reportedUserId, ReportCategory category,
                                                 String description, List<String> suspiciousWords) {
        ReportSeverity severity = determineSeverityFromWords(suspiciousWords);
        
        ContentReport report = new ContentReport(
            id, postId, null, reportedUserId, 
            category, severity, description, ReportStatus.PENDING,
            true, suspiciousWords, LocalDateTime.now()
        );
        
        // Add initial action
        report.addAction(ReportAction.create(
            ActionType.ESCALATED, 
            null, 
            "Automatically detected suspicious content"
        ));
        
        // Emit events
        report.recordEvent(new ReportCreatedEvent(
            id.value().toString(),
            postId.value().toString(),
            category.name(),
            severity.name(),
            true
        ));
        
        report.recordEvent(new SuspiciousContentDetectedEvent(
            id.value().toString(),
            postId.value().toString(),
            reportedUserId.value().toString(),
            suspiciousWords,
            category.name(),
            severity.name(),
            description
        ));
        
        return report;
    }

    /**
     * Restore aggregate from persistence (without events)
     */
    public static ContentReport restore(ReportId id, PostId postId, UserId reporterUserId,
                                      UserId reportedUserId, ReportCategory category,
                                      ReportSeverity severity, String description, 
                                      ReportStatus status, boolean autoDetected,
                                      List<String> suspiciousWords, List<ReportAction> existingActions,
                                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        ContentReport report = new ContentReport(
            id, postId, reporterUserId, reportedUserId, 
            category, severity, description, status,
            autoDetected, suspiciousWords, createdAt
        );
        
        report.updatedAt = updatedAt != null ? updatedAt : createdAt;
        
        if (existingActions != null && !existingActions.isEmpty()) {
            report.actions.addAll(existingActions);
        }
        
        return report;
    }

    /**
     * Add an action to the report
     */
    public void addAction(ReportAction action) {
        this.actions.add(Objects.requireNonNull(action, "action required"));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update report status
     */
    public void updateStatus(ReportStatus newStatus, UserId performedBy, String reason) {
        if (this.status != newStatus) {
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
            
            ActionType actionType = switch (newStatus) {
                case APPROVED -> ActionType.APPROVED;
                case REJECTED -> ActionType.REJECTED;
                case RESOLVED -> ActionType.RESOLVED;
                case ESCALATED -> ActionType.ESCALATED;
                case UNDER_REVIEW -> ActionType.REVIEWED;
                default -> ActionType.REVIEWED;
            };
            
            addAction(ReportAction.create(actionType, performedBy, reason));
        }
    }

    /**
     * Mark as auto-detected (for migration purposes)
     */
    public void markAsAutoDetected() {
        if (!this.autoDetected) {
            addAction(ReportAction.create(
                ActionType.ESCALATED, 
                null, 
                "Report marked as auto-detected"
            ));
        }
    }

    /**
     * Determine severity based on category
     */
    private static ReportSeverity determineSeverity(ReportCategory category) {
        return switch (category) {
            case HATE_SPEECH, VIOLENCE, RACISM, DISCRIMINATION -> ReportSeverity.HIGH;
            case HARASSMENT, SEXUAL_CONTENT, SELF_HARM -> ReportSeverity.MEDIUM;
            case SPAM, INAPPROPRIATE, MISINFORMATION, COPYRIGHT -> ReportSeverity.LOW;
            case OTHER -> ReportSeverity.LOW;
        };
    }

    /**
     * Determine severity based on suspicious words count
     */
    private static ReportSeverity determineSeverityFromWords(List<String> suspiciousWords) {
        if (suspiciousWords == null || suspiciousWords.isEmpty()) {
            return ReportSeverity.LOW;
        }
        
        int count = suspiciousWords.size();
        if (count >= 3) return ReportSeverity.CRITICAL;
        if (count >= 2) return ReportSeverity.HIGH;
        return ReportSeverity.MEDIUM;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " required");
        return value;
    }

    // Getters
    public ReportId getId() { return id; }
    public PostId getPostId() { return postId; }
    public UserId getReporterUserId() { return reporterUserId; }
    public UserId getReportedUserId() { return reportedUserId; }
    public ReportCategory getCategory() { return category; }
    public ReportSeverity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public ReportStatus getStatus() { return status; }
    public boolean isAutoDetected() { return autoDetected; }
    public List<String> getSuspiciousWords() { return Collections.unmodifiableList(suspiciousWords); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<ReportAction> getActions() { return Collections.unmodifiableList(actions); }
}