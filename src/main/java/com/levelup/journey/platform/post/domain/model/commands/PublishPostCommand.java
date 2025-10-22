package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/** Command: Publish post */
public record PublishPostCommand(PostId id, CommunityId communityId, UserId authorId, String title, String content) {
    public PublishPostCommand {
        if (id == null) throw new IllegalArgumentException("id required");
        if (communityId == null) throw new IllegalArgumentException("communityId required");
        if (authorId == null) throw new IllegalArgumentException("authorId required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content required");
    }
}

