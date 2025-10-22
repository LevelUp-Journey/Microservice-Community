package com.levelup.journey.platform.moderation.domain.services;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.queries.*;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * Query service for Report operations
 */
public interface ReportQueryService {
    
    /**
     * Handle get report by ID
     * @param query get report by ID query
     * @return report if found
     */
    Optional<ContentReport> handle(GetReportByIdQuery query);
    
    /**
     * Handle get all reports
     * @param query get all reports query
     * @return list of all reports
     */
    List<ContentReport> handle(GetAllReportsQuery query);
    
    /**
     * Handle get reports by post ID
     * @param query get reports by post ID query
     * @return list of reports for the post
     */
    List<ContentReport> handle(GetReportsByPostIdQuery query);
    
    /**
     * Handle get reports by category
     * @param query get reports by category query
     * @return list of reports in the category
     */
    List<ContentReport> handle(GetReportsByCategoryQuery query);
    
    /**
     * Handle get reports by user ID
     * @param query get reports by user ID query
     * @return list of reports for the user
     */
    List<ContentReport> handle(GetReportsByUserIdQuery query);
    
    /**
     * Handle get pending reports
     * @param query get pending reports query
     * @return list of pending reports
     */
    List<ContentReport> handle(GetPendingReportsQuery query);
    
    /**
     * Handle get auto-detected reports
     * @param query get auto-detected reports query
     * @return list of auto-detected reports
     */
    List<ContentReport> handle(GetAutoDetectedReportsQuery query);
    
    /**
     * Check if a report exists
     * @param reportId report ID
     * @return true if exists, false otherwise
     */
    boolean reportExists(ReportId reportId);
    
    /**
     * Count reports by category
     * @param category report category
     * @return number of reports in the category
     */
    long countReportsByCategory(ReportCategory category);
    
    /**
     * Count reports by user
     * @param userId user ID
     * @return number of reports for the user
     */
    long countReportsByUser(UserId userId);
    
    /**
     * Count pending reports
     * @return number of pending reports
     */
    long countPendingReports();
    
    /**
     * Count auto-detected reports
     * @return number of auto-detected reports
     */
    long countAutoDetectedReports();
}