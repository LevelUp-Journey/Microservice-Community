package com.levelup.journey.platform.community.post.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating an existing post.
 * Represents the data required to update a post via REST API.
 */
@Schema(description = "Request payload for updating an existing post")
public record UpdatePostResource(
        @NotBlank(message = "Title is required")
        @Schema(description = "Updated title of the post", example = "Updated: Introduction to DDD", maxLength = 255)
        String title,

        @NotBlank(message = "Body is required")
        @Schema(description = "Updated body content of the post", example = "This is an updated introduction...")
        String body,

        @Schema(description = "Updated image URL (optional)", example = "https://example.com/new-image.jpg")
        String imageUrl
) {
}
