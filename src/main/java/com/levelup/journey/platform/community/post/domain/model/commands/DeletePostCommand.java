package com.levelup.journey.platform.community.post.domain.model.commands;

/**
 * Command to perform logical deletion of a post.
 * Only the author (Docente) can delete their own posts.
 *
 * @param postId the ID of the post to delete
 * @param userId the ID of the user requesting the deletion (for authorization)
 */
public record DeletePostCommand(
        Long postId,
        Long userId
) {
    public DeletePostCommand {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Post ID must be a positive number");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}
