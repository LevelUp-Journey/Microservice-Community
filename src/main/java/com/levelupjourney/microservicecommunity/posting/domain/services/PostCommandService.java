package com.levelupjourney.microservicecommunity.posting.domain.services;

import com.levelupjourney.microservicecommunity.posting.domain.model.commands.CreatePostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.model.commands.DeletePostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.model.commands.EditPostCommand;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;

import java.util.Optional;

/**
 * Domain service interface for post command operations.
 * Follows CQRS pattern by separating command operations.
 */
public interface PostCommandService {
    
    /**
     * Handles the creation of a new post.
     * 
     * @param command the create post command
     * @return the ID of the created post
     */
    Optional<PostId> handle(CreatePostCommand command);
    
    /**
     * Handles editing an existing post.
     * 
     * @param command the edit post command
     * @return the ID of the edited post if successful
     */
    Optional<PostId> handle(EditPostCommand command);
    
    /**
     * Handles deleting a post.
     * 
     * @param command the delete post command
     * @return the ID of the deleted post if successful
     */
    Optional<PostId> handle(DeletePostCommand command);
}
