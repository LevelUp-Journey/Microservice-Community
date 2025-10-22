package com.levelup.journey.platform.moderation.domain.model.repositories;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportId;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ContentReport aggregate
 */
public interface ReportRepository {
    
    /**
     * Save a report
     * @param report the report to save
     * @return saved report
     */
    ContentReport save(ContentReport report);
    
    /**
     * Find report by ID
     * @param id report ID
     * @return report if found
     */
    Optional<ContentReport> findById(ReportId id);
    
    /**
     * Find all reports for a specific post
     * @param postId post ID
     * @return list of reports for the post
     */
    List<ContentReport> findByPostId(PostId postId);
    
    /**
     * Find all reports by category
     * @param category report category
     * @return list of reports in the category
     */
    List<ContentReport> findByCategory(ReportCategory category);
    
    /**
     * Find all reports
     * @return list of all reports
     */
    List<ContentReport> findAll();
    
    /**
     * Find all reports by reported user ID
     * @param userId reported user ID
     * @return list of reports for the user
     */
    List<ContentReport> findByReportedUserId(UserId userId);
    
    /**
     * Find all auto-detected reports
     * @return list of auto-detected reports
     */
    List<ContentReport> findAutoDetectedReports();
    
    /**
     * Check if a report exists by ID
     * @param id report ID
     * @return true if exists, false otherwise
     */
    boolean existsById(ReportId id);
    
    /**
     * Delete report by ID
     * @param id report ID
     */
    void deleteById(ReportId id);
}