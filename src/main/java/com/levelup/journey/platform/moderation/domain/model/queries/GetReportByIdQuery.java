package com.levelup.journey.platform.moderation.domain.model.queries;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportId;

public record GetReportByIdQuery(ReportId reportId) {
    public GetReportByIdQuery {
        if (reportId == null) throw new IllegalArgumentException("ReportId cannot be null");
    }
}