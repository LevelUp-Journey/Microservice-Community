package com.levelup.journey.platform.moderation.domain.model.queries;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.PostId;

public record GetReportsByPostIdQuery(PostId postId) {
    public GetReportsByPostIdQuery {
        if (postId == null) throw new IllegalArgumentException("PostId cannot be null");
    }
}