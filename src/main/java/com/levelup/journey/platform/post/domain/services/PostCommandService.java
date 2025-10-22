package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.PublishPostCommand;

import java.util.Optional;

/**
 * Post Command Service
 * Defines business operations for managing posts
 */
public interface PostCommandService {

    /**
     * Handles the command to publish a new post
     * @param command the publish post command
     * @return the published post
     */
    Optional<Post> handle(PublishPostCommand command);

    /**
     * Handles the command to add a comment to a post
     * @param command the add comment command
     * @return the updated post with the new comment
     */
    Optional<Post> handle(AddCommentCommand command);
}
