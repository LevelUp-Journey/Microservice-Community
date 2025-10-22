package com.levelup.journey.platform.moderation.domain.model.queries;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportSeverity;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportStatus;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId;

public record GetReportsByUserIdQuery(
    UserId userId,
    ReportCategory category,
    ReportSeverity severity,
    ReportStatus status,
    Integer limit,
    Integer offset
) {}