package com.levelup.journey.platform.moderation.application.internal.queryservices;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.queries.*;
import com.levelup.journey.platform.moderation.domain.model.repositories.ReportRepository;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.*;
import com.levelup.journey.platform.moderation.domain.services.ReportQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportQueryServiceImpl implements ReportQueryService {

    private final ReportRepository reportRepository;

    public ReportQueryServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Optional<ContentReport> handle(GetReportByIdQuery query) {
        return reportRepository.findById(query.reportId());
    }

    @Override
    public List<ContentReport> handle(GetAllReportsQuery query) {
        return reportRepository.findAll();
    }

    @Override
    public List<ContentReport> handle(GetReportsByPostIdQuery query) {
        return reportRepository.findByPostId(query.postId());
    }

    @Override
    public List<ContentReport> handle(GetReportsByCategoryQuery query) {
        List<ContentReport> reports = reportRepository.findByCategory(query.category());
        
        // Apply filters if specified
        if (query.severity() != null) {
            reports = reports.stream()
                .filter(report -> report.getSeverity() == query.severity())
                .collect(Collectors.toList());
        }
        
        if (query.status() != null) {
            reports = reports.stream()
                .filter(report -> report.getStatus() == query.status())
                .collect(Collectors.toList());
        }
        
        if (query.autoDetected() != null) {
            reports = reports.stream()
                .filter(report -> report.isAutoDetected() == query.autoDetected())
                .collect(Collectors.toList());
        }
        
        // Apply pagination if specified
        if (query.limit() != null && query.limit() > 0) {
            int offset = query.offset() != null ? query.offset() : 0;
            return reports.stream()
                .skip(offset)
                .limit(query.limit())
                .collect(Collectors.toList());
        }
        
        return reports;
    }

    @Override
    public List<ContentReport> handle(GetReportsByUserIdQuery query) {
        List<ContentReport> reports = reportRepository.findByReportedUserId(query.userId());
        
        // Apply filters if specified
        if (query.category() != null) {
            reports = reports.stream()
                .filter(report -> report.getCategory() == query.category())
                .collect(Collectors.toList());
        }
        
        if (query.severity() != null) {
            reports = reports.stream()
                .filter(report -> report.getSeverity() == query.severity())
                .collect(Collectors.toList());
        }
        
        if (query.status() != null) {
            reports = reports.stream()
                .filter(report -> report.getStatus() == query.status())
                .collect(Collectors.toList());
        }
        
        // Apply pagination if specified
        if (query.limit() != null && query.limit() > 0) {
            int offset = query.offset() != null ? query.offset() : 0;
            return reports.stream()
                .skip(offset)
                .limit(query.limit())
                .collect(Collectors.toList());
        }
        
        return reports;
    }

    @Override
    public List<ContentReport> handle(GetPendingReportsQuery query) {
        List<ContentReport> allReports = getAllReports();
        
        List<ContentReport> pendingReports = allReports.stream()
            .filter(report -> report.getStatus() == ReportStatus.PENDING || 
                            report.getStatus() == ReportStatus.UNDER_REVIEW)
            .collect(Collectors.toList());
        
        // Apply filters if specified
        if (query.category() != null) {
            pendingReports = pendingReports.stream()
                .filter(report -> report.getCategory() == query.category())
                .collect(Collectors.toList());
        }
        
        if (query.severity() != null) {
            pendingReports = pendingReports.stream()
                .filter(report -> report.getSeverity() == query.severity())
                .collect(Collectors.toList());
        }
        
        if (query.autoDetected() != null) {
            pendingReports = pendingReports.stream()
                .filter(report -> report.isAutoDetected() == query.autoDetected())
                .collect(Collectors.toList());
        }
        
        // Sort by severity (CRITICAL first) and then by creation date
        pendingReports = pendingReports.stream()
            .sorted((r1, r2) -> {
                // First sort by severity priority
                int severityCompare = getSeverityPriority(r2.getSeverity()) - 
                                    getSeverityPriority(r1.getSeverity());
                if (severityCompare != 0) {
                    return severityCompare;
                }
                // Then by creation date (newest first)
                return r2.getCreatedAt().compareTo(r1.getCreatedAt());
            })
            .collect(Collectors.toList());
        
        // Apply pagination if specified
        if (query.limit() != null && query.limit() > 0) {
            int offset = query.offset() != null ? query.offset() : 0;
            return pendingReports.stream()
                .skip(offset)
                .limit(query.limit())
                .collect(Collectors.toList());
        }
        
        return pendingReports;
    }

    @Override
    public List<ContentReport> handle(GetAutoDetectedReportsQuery query) {
        List<ContentReport> autoDetectedReports = reportRepository.findAutoDetectedReports();
        
        // Apply filters if specified
        if (query.category() != null) {
            autoDetectedReports = autoDetectedReports.stream()
                .filter(report -> report.getCategory() == query.category())
                .collect(Collectors.toList());
        }
        
        if (query.severity() != null) {
            autoDetectedReports = autoDetectedReports.stream()
                .filter(report -> report.getSeverity() == query.severity())
                .collect(Collectors.toList());
        }
        
        if (query.hasMinWords() != null && query.hasMinWords() > 0) {
            autoDetectedReports = autoDetectedReports.stream()
                .filter(report -> report.getSuspiciousWords().size() >= query.hasMinWords())
                .collect(Collectors.toList());
        }
        
        // Sort by severity and suspicious words count
        autoDetectedReports = autoDetectedReports.stream()
            .sorted((r1, r2) -> {
                // First sort by severity priority
                int severityCompare = getSeverityPriority(r2.getSeverity()) - 
                                    getSeverityPriority(r1.getSeverity());
                if (severityCompare != 0) {
                    return severityCompare;
                }
                // Then by suspicious words count (more words first)
                int wordsCompare = r2.getSuspiciousWords().size() - r1.getSuspiciousWords().size();
                if (wordsCompare != 0) {
                    return wordsCompare;
                }
                // Finally by creation date (newest first)
                return r2.getCreatedAt().compareTo(r1.getCreatedAt());
            })
            .collect(Collectors.toList());
        
        // Apply pagination if specified
        if (query.limit() != null && query.limit() > 0) {
            int offset = query.offset() != null ? query.offset() : 0;
            return autoDetectedReports.stream()
                .skip(offset)
                .limit(query.limit())
                .collect(Collectors.toList());
        }
        
        return autoDetectedReports;
    }

    @Override
    public boolean reportExists(ReportId reportId) {
        return reportRepository.existsById(reportId);
    }

    @Override
    public long countReportsByCategory(ReportCategory category) {
        return reportRepository.findByCategory(category).size();
    }

    @Override
    public long countReportsByUser(UserId userId) {
        return reportRepository.findByReportedUserId(userId).size();
    }

    @Override
    public long countPendingReports() {
        return getAllReports().stream()
            .filter(report -> report.getStatus() == ReportStatus.PENDING || 
                            report.getStatus() == ReportStatus.UNDER_REVIEW)
            .count();
    }

    @Override
    public long countAutoDetectedReports() {
        return reportRepository.findAutoDetectedReports().size();
    }

    private List<ContentReport> getAllReports() {
        // This is a simplified approach. In a real implementation, you might want to
        // implement a more efficient way to get all reports, possibly with pagination
        // at the repository level or using a separate query method.
        List<ContentReport> allReports = reportRepository.findAutoDetectedReports();
        
        // Add manual reports by checking each category
        for (ReportCategory category : ReportCategory.values()) {
            List<ContentReport> categoryReports = reportRepository.findByCategory(category);
            for (ContentReport report : categoryReports) {
                if (!report.isAutoDetected() && !allReports.contains(report)) {
                    allReports.add(report);
                }
            }
        }
        
        return allReports;
    }

    private int getSeverityPriority(ReportSeverity severity) {
        return switch (severity) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }
}