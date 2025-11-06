package com.levelup.journey.platform.moderation.domain.model.commands;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.UserId;

public record AnalyzeContentCommand(
    PostId postId,
    UserId reportedUserId,
    String content
) {
    public AnalyzeContentCommand {
        if (postId == null) throw new IllegalArgumentException("PostId cannot be null");
        if (reportedUserId == null) throw new IllegalArgumentException("ReportedUserId cannot be null");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Content cannot be null or empty");
    }
}