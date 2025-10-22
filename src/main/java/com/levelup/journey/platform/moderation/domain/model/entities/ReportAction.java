package com.levelup.journey.platform.moderation.domain.model.entities;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ActionType;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ReportAction Entity within the ContentReport Aggregate
 * Represents actions performed on a report
 */
public final class ReportAction {
    private final ActionType actionType;
    private final String reason;
    private final UserId performedBy;
    private final LocalDateTime performedAt;

    public ReportAction(ActionType actionType, String reason, UserId performedBy, LocalDateTime performedAt) {
        this.actionType = Objects.requireNonNull(actionType, "actionType required");
        this.reason = requireText(reason, "reason");
        this.performedBy = performedBy; // Can be null for system actions
        this.performedAt = Objects.requireNonNullElse(performedAt, LocalDateTime.now());
    }

    public static ReportAction create(ActionType actionType, UserId performedBy, String reason) {
        return new ReportAction(actionType, reason, performedBy, LocalDateTime.now());
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " required");
        return value;
    }

    // Getters
    public ActionType getActionType() { return actionType; }
    public String getReason() { return reason; }
    public UserId getPerformedBy() { return performedBy; }
    public LocalDateTime getPerformedAt() { return performedAt; }
}