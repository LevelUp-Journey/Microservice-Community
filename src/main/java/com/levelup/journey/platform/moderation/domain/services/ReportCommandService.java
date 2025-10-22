package com.levelup.journey.platform.moderation.domain.services;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.commands.AnalyzeContentCommand;
import com.levelup.journey.platform.moderation.domain.model.commands.CreateReportCommand;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportId;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Command service for Report operations
 */
public interface ReportCommandService {
    
    /**
     * Handle manual report creation
     * @param command create report command
     * @return created report ID
     */
    ReportId handle(CreateReportCommand command);
    
    /**
     * Handle content analysis and auto-detection
     * @param command analyze content command
     * @return created report ID if suspicious content detected, null otherwise
     */
    ReportId handle(AnalyzeContentCommand command);
    
    /**
     * Approve a report
     * @param reportId report ID
     * @param moderatorId moderator ID
     * @param reason reason for approval
     */
    void approveReport(ReportId reportId, UserId moderatorId, String reason);
    
    /**
     * Reject a report
     * @param reportId report ID
     * @param moderatorId moderator ID
     * @param reason reason for rejection
     */
    void rejectReport(ReportId reportId, UserId moderatorId, String reason);
    
    /**
     * Escalate a report
     * @param reportId report ID
     * @param moderatorId moderator ID
     * @param reason reason for escalation
     */
    void escalateReport(ReportId reportId, UserId moderatorId, String reason);
    
    /**
     * Resolve a report
     * @param reportId report ID
     * @param moderatorId moderator ID
     * @param reason reason for resolution
     */
    void resolveReport(ReportId reportId, UserId moderatorId, String reason);
    
    /**
     * Delete a report
     * @param reportId report ID
     * @param moderatorId moderator ID
     */
    void deleteReport(ReportId reportId, UserId moderatorId);
}