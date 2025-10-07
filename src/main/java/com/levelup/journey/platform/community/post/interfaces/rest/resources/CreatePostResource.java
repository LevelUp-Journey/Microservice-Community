package com.levelup.journey.platform.community.post.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for creating a new post.
 * Represents the data required to create a post via REST API.
 */
@Schema(description = "Request payload for creating a new post")
public record CreatePostResource(
        @NotNull(message = "User ID is required")
        @Schema(description = "ID of the user creating the post (must be a Docente)", example = "1")
        Long userId,

        @NotBlank(message = "Title is required")
        @Schema(description = "Title of the post", example = "Introduction to Domain-Driven Design", maxLength = 255)
        String title,

        @NotBlank(message = "Body is required")
        @Schema(description = "Body content of the post", example = "This is an introduction to DDD principles...")
        String body,

        @Schema(description = "Optional image URL for the post", example = "https://example.com/image.jpg")
        String imageUrl
) {
}
