package com.levelup.journey.platform.community.post.domain.model.commands;

/**
 * Command to create a new post.
 * Only users with DOCENTE role can create posts.
 *
 * @param userId the ID of the user creating the post (must be a Docente)
 * @param title the title of the post
 * @param body the body content of the post
 * @param imageUrl optional image URL for the post (must be validated)
 */
public record CreatePostCommand(
        Long userId,
        String title,
        String body,
        String imageUrl
) {
    public CreatePostCommand {
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
