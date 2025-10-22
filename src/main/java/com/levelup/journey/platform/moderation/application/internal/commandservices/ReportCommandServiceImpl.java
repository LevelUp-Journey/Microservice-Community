package com.levelup.journey.platform.moderation.application.internal.commandservices;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.commands.AnalyzeContentCommand;
import com.levelup.journey.platform.moderation.domain.model.commands.CreateReportCommand;
import com.levelup.journey.platform.moderation.domain.model.repositories.ReportRepository;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.*;
import com.levelup.journey.platform.moderation.domain.services.ReportCommandService;
import com.levelup.journey.platform.moderation.infrastructure.services.ContentAnalysisServiceImpl;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReportCommandServiceImpl implements ReportCommandService {

    private final ReportRepository reportRepository;
    private final ContentAnalysisServiceImpl contentAnalysisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReportCommandServiceImpl(ReportRepository reportRepository,
                                  ContentAnalysisServiceImpl contentAnalysisService,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.reportRepository = reportRepository;
        this.contentAnalysisService = contentAnalysisService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ReportId handle(CreateReportCommand command) {
        // Validate that the post and users exist (would need to call other bounded contexts)
        validateCommand(command);

        // Create report ID
        ReportId reportId = new ReportId(UUID.randomUUID());

        // Create the report
        ContentReport report = ContentReport.create(
            reportId,
            command.postId(),
            command.reporterUserId(),
            command.reportedUserId(),
            command.category(),
            command.description()
        );

        // Save the report
        ContentReport savedReport = reportRepository.save(report);

        // Publish events
        publishDomainEvents(savedReport);

        return savedReport.getId();
    }

    @Override
    public ReportId handle(AnalyzeContentCommand command) {
        // Analyze content for suspicious words (using empty string for title since we only have content)
        List<String> suspiciousWords = contentAnalysisService.analyzeContent("", command.content());

        // If no suspicious content found, return null (no report created)
        if (suspiciousWords.isEmpty()) {
            return null;
        }

        // Determine category based on suspicious words
        ReportCategory category = contentAnalysisService.categorizeContent(suspiciousWords);

        // Create report ID
        ReportId reportId = new ReportId(UUID.randomUUID());

        // Create auto-detected report
        ContentReport report = ContentReport.createAutoDetected(
            reportId,
            command.postId(),
            command.reportedUserId(),
            category,
            "Auto-detected suspicious content: " + String.join(", ", suspiciousWords),
            suspiciousWords
        );

        // Save the report
        ContentReport savedReport = reportRepository.save(report);

        // Publish events
        publishDomainEvents(savedReport);

        return savedReport.getId();
    }

    @Override
    public void approveReport(ReportId reportId, UserId moderatorId, String reason) {
        ContentReport report = getReportById(reportId);
        report.updateStatus(ReportStatus.APPROVED, moderatorId, reason);
        
        ContentReport savedReport = reportRepository.save(report);
        publishDomainEvents(savedReport);
    }

    @Override
    public void rejectReport(ReportId reportId, UserId moderatorId, String reason) {
        ContentReport report = getReportById(reportId);
        report.updateStatus(ReportStatus.REJECTED, moderatorId, reason);
        
        ContentReport savedReport = reportRepository.save(report);
        publishDomainEvents(savedReport);
    }

    @Override
    public void escalateReport(ReportId reportId, UserId moderatorId, String reason) {
        ContentReport report = getReportById(reportId);
        report.updateStatus(ReportStatus.ESCALATED, moderatorId, reason);
        
        ContentReport savedReport = reportRepository.save(report);
        publishDomainEvents(savedReport);
    }

    @Override
    public void resolveReport(ReportId reportId, UserId moderatorId, String reason) {
        ContentReport report = getReportById(reportId);
        report.updateStatus(ReportStatus.RESOLVED, moderatorId, reason);
        
        ContentReport savedReport = reportRepository.save(report);
        publishDomainEvents(savedReport);
    }

    @Override
    public void deleteReport(ReportId reportId, UserId moderatorId) {
        // Verify report exists and can be deleted
        ContentReport report = getReportById(reportId);
        
        // Add deletion action before removing
        report.addAction(com.levelup.journey.platform.moderation.domain.model.entities.ReportAction.create(
            ActionType.REMOVED_CONTENT, 
            moderatorId, 
            "Report deleted by moderator"
        ));
        
        // Save final state and publish events
        ContentReport savedReport = reportRepository.save(report);
        publishDomainEvents(savedReport);
        
        // Delete the report
        reportRepository.deleteById(reportId);
    }

    private ContentReport getReportById(ReportId reportId) {
        return reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId.value()));
    }

    private void validateCommand(CreateReportCommand command) {
        // Basic validation
        if (command.reporterUserId().equals(command.reportedUserId())) {
            throw new IllegalArgumentException("User cannot report themselves");
        }
        
        // Additional validations could include:
        // - Check if post exists (call to Post bounded context)
        // - Check if users exist (call to User bounded context)
        // - Check for duplicate reports
        // - Rate limiting checks
    }

    private void publishDomainEvents(ContentReport report) {
        List<com.levelup.journey.platform.moderation.domain.model.events.DomainEvent> events = report.pullDomainEvents();
        
        for (var event : events) {
            try {
                kafkaTemplate.send("moderation-events", event);
            } catch (Exception e) {
                // Log error but don't fail the operation
                System.err.println("Failed to publish event: " + event.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }
    }
}