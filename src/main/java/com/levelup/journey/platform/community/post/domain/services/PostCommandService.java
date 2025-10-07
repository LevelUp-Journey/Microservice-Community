package com.levelup.journey.platform.community.post.domain.services;

import com.levelup.journey.platform.community.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.community.post.domain.model.commands.CreatePostCommand;
import com.levelup.journey.platform.community.post.domain.model.commands.DeletePostCommand;
import com.levelup.journey.platform.community.post.domain.model.commands.UpdatePostCommand;

import java.util.Optional;

/**
 * Domain service interface for Post command operations.
 * Handles all state-changing operations for posts.
 */
public interface PostCommandService {

    /**
     * Handles the creation of a new post.
     * Only users with DOCENTE role can create posts.
     *
     * @param command the create post command containing post data
     * @return Optional containing the created Post, or empty if creation failed
     */
    Optional<Post> handle(CreatePostCommand command);

    /**
     * Handles updating an existing post.
     * Only the post author can update their post.
     *
     * @param command the update post command containing updated data
     * @return Optional containing the updated Post, or empty if update failed
     */
    Optional<Post> handle(UpdatePostCommand command);

    /**
     * Handles logical deletion of a post.
     * Only the post author can delete their post.
     *
     * @param command the delete post command
     * @return Optional containing the deleted Post, or empty if deletion failed
     */
    Optional<Post> handle(DeletePostCommand command);
}
