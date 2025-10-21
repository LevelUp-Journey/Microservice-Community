package com.levelup.journey.platform.domain.model.commands;

import com.levelup.journey.platform.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.domain.model.valueobjects.UserId;

/** Command: Publicar post */
public record PublishPostCommand(PostId id, CommunityId communityId, UserId authorId, String title, String content) {
    public PublishPostCommand {
        if (id == null) throw new IllegalArgumentException("id requerido");
        if (communityId == null) throw new IllegalArgumentException("communityId requerido");
        if (authorId == null) throw new IllegalArgumentException("authorId requerido");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title requerido");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content requerido");
    }
}

