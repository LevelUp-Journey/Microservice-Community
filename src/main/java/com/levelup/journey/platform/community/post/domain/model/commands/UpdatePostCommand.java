package com.levelup.journey.platform.community.post.domain.model.commands;

/**
 * Command to update an existing post.
 * Only the author (Docente) can update their own posts.
 *
 * @param postId the ID of the post to update
 * @param userId the ID of the user requesting the update (for authorization)
 * @param title the new title of the post
 * @param body the new body content of the post
 * @param imageUrl the new image URL (must be validated)
 */
public record UpdatePostCommand(
        Long postId,
        Long userId,
        String title,
        String body,
        String imageUrl
) {
    public UpdatePostCommand {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Post ID must be a positive number");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Body cannot be null or blank");
        }
    }
}
