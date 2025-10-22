package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/** Command: Publish post */
public record PublishPostCommand(CommunityId communityId, UserId authorId, String title, String content, String imageUrl) {
    public PublishPostCommand {
        if (communityId == null) throw new IllegalArgumentException("communityId required");
        if (authorId == null) throw new IllegalArgumentException("authorId required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content required");
        // imageUrl is optional
    }
}

