package com.levelup.journey.platform.moderation.domain.model.queries;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportSeverity;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportStatus;

public record GetReportsByCategoryQuery(
    ReportCategory category,
    ReportSeverity severity,
    ReportStatus status,
    Boolean autoDetected,
    Integer limit,
    Integer offset
) {
    public GetReportsByCategoryQuery {
        if (category == null) throw new IllegalArgumentException("ReportCategory cannot be null");
    }
}