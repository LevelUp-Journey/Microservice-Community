package com.levelupjourney.microservicecommunity.interaction.domain.services;

import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.LikePostCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.UnlikePostCommand;

/**
 * Domain service interface for like command operations.
 */
public interface LikeCommandService {
    
    /**
     * Handles liking a post.
     * 
     * @param command the like post command
     * @return true if the post was successfully liked, false if already liked
     */
    boolean handle(LikePostCommand command);
    
    /**
     * Handles unliking a post.
     * 
     * @param command the unlike post command
     * @return true if the post was successfully unliked, false if not previously liked
     */
    boolean handle(UnlikePostCommand command);
}
