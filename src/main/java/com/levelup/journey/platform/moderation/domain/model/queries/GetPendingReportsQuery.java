package com.levelup.journey.platform.moderation.domain.model.queries;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportSeverity;

public record GetPendingReportsQuery(
    ReportCategory category,
    ReportSeverity severity,
    Boolean autoDetected,
    Integer limit,
    Integer offset
) {}