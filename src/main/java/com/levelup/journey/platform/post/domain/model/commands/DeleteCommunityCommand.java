package com.levelup.journey.platform.post.domain.model.commands;

import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;

/**
 * Delete Community Command
 * Command to delete a community with authorization checks
 */
public record DeleteCommunityCommand(CommunityId communityId, UserId requesterId) {

    public DeleteCommunityCommand {
        if (communityId == null) throw new IllegalArgumentException("communityId cannot be null");
        if (requesterId == null) throw new IllegalArgumentException("requesterId cannot be null");
    }

    public static DeleteCommunityCommand of(String communityId, String requesterId) {
        return new DeleteCommunityCommand(CommunityId.of(communityId), UserId.of(requesterId));
    }
}