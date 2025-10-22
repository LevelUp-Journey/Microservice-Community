package com.levelup.journey.platform.moderation.domain.model.queries;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportSeverity;

public record GetAutoDetectedReportsQuery(
    ReportCategory category,
    ReportSeverity severity,
    Integer hasMinWords,
    Integer limit,
    Integer offset
) {}