package com.levelup.journey.platform.moderation.interfaces.rest.transform;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.commands.AnalyzeContentCommand;
import com.levelup.journey.platform.moderation.domain.model.commands.CreateReportCommand;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.*;
import com.levelup.journey.platform.moderation.interfaces.rest.resources.*;

import java.util.stream.Collectors;

/**
 * Assembler for transforming between domain objects and REST resources
 */
public class ReportResourceAssembler {

    /**
     * Transform ContentReport domain object to ReportResource
     */
    public static ReportResource toResourceFromAggregate(ContentReport report) {
        var actions = report.getActions().stream()
            .map(action -> new ReportResource.ReportActionResource(
                action.getActionType().name(),
                action.getReason(),
                action.getPerformedBy() != null ? action.getPerformedBy().value() : null,
                action.getPerformedAt()
            ))
            .collect(Collectors.toList());

        return new ReportResource(
            report.getId().value(),
            report.getPostId().value(),
            report.getReporterUserId() != null ? report.getReporterUserId().value() : null,
            report.getReportedUserId().value(),
            report.getCategory().name(),
            report.getSeverity().name(),
            report.getDescription(),
            report.getStatus().name(),
            report.isAutoDetected(),
            report.getSuspiciousWords(),
            report.getCreatedAt(),
            report.getUpdatedAt(),
            actions
        );
    }

    /**
     * Transform CreateReportResource to CreateReportCommand
     */
    public static CreateReportCommand toCommandFromResource(CreateReportResource resource, UserId reporterUserId) {
        return new CreateReportCommand(
            new PostId(resource.postId()),
            reporterUserId,
            new UserId(resource.reportedUserId()),
            ReportCategory.valueOf(resource.category().toUpperCase()),
            resource.description()
        );
    }

    /**
     * Transform AnalyzeContentResource to AnalyzeContentCommand
     */
    public static AnalyzeContentCommand toCommandFromResource(AnalyzeContentResource resource) {
        return new AnalyzeContentCommand(
            new PostId(resource.postId()),
            new UserId(resource.userId()),
            resource.content()
        );
    }

    /**
     * Transform status string to ReportStatus enum
     */
    public static ReportStatus toReportStatus(String status) {
        try {
            return ReportStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid report status: " + status);
        }
    }

    /**
     * Transform category string to ReportCategory enum
     */
    public static ReportCategory toReportCategory(String category) {
        try {
            return ReportCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid report category: " + category);
        }
    }

    /**
     * Transform severity string to ReportSeverity enum
     */
    public static ReportSeverity toReportSeverity(String severity) {
        try {
            return ReportSeverity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid report severity: " + severity);
        }
    }
}