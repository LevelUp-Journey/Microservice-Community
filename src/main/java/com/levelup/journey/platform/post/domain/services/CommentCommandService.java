package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
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
}
