package com.levelup.journey.platform.moderation.domain.model.commands;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId;

public record CreateReportCommand(
    PostId postId,
    UserId reporterUserId,
    UserId reportedUserId,
    ReportCategory category,
    String description
) {
    public CreateReportCommand {
        if (postId == null) throw new IllegalArgumentException("PostId cannot be null");
        if (reporterUserId == null) throw new IllegalArgumentException("ReporterUserId cannot be null");
        if (reportedUserId == null) throw new IllegalArgumentException("ReportedUserId cannot be null");
        if (category == null) throw new IllegalArgumentException("ReportCategory cannot be null");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description cannot be null or empty");
    }
}