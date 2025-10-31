package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.DeleteCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.EditCommentCommand;
import com.levelup.journey.platform.post.domain.model.entities.Comment;

import java.util.Optional;

/**
 * Comment Command Service
 * Defines business operations for managing comments
 */
public interface CommentCommandService {

    /**
     * Handles the command to add a comment to a post
     * @param command the add comment command
     * @return the created comment
     */
    Optional<Comment> handle(AddCommentCommand command);

    /**
     * Handles the command to delete a comment from a post
     * @param command the delete comment command
     * @return true if the comment was deleted, false otherwise
     */
    boolean handle(DeleteCommentCommand command);

    /**
     * Handles the command to edit a comment's content
     * @param command the edit comment command
     * @return the updated comment if successful, empty otherwise
     */
    Optional<Comment> handle(EditCommentCommand command);
}
