package com.levelupjourney.microservicecommunity.interaction.domain.services;

import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.AddCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.DeleteCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.EditCommentCommand;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.CommentId;

import java.util.Optional;

/**
 * Domain service interface for comment command operations.
 */
public interface CommentCommandService {
    
    /**
     * Handles adding a new comment to a post.
     * 
     * @param command the add comment command
     * @return the ID of the created comment
     */
    Optional<CommentId> handle(AddCommentCommand command);
    
    /**
     * Handles editing an existing comment.
     * 
     * @param command the edit comment command
     * @return the ID of the edited comment
     */
    Optional<CommentId> handle(EditCommentCommand command);
    
    /**
     * Handles deleting a comment.
     * 
     * @param command the delete comment command
     * @return the ID of the deleted comment
     */
    Optional<CommentId> handle(DeleteCommentCommand command);
}
