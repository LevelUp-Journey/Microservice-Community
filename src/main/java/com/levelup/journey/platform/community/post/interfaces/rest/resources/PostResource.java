package com.levelup.journey.platform.community.post.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * Resource representing a post in REST API responses.
 * Contains all post data for client consumption.
 */
@Schema(description = "Post data transfer object")
public record PostResource(
        @Schema(description = "Unique identifier of the post", example = "1")
        Long id,

        @Schema(description = "ID of the user who created the post", example = "1")
        Long userId,

        @Schema(description = "Title of the post", example = "Introduction to Domain-Driven Design")
        String title,

        @Schema(description = "Body content of the post", example = "This is an introduction to DDD principles...")
        String body,

        @Schema(description = "Image URL if present", example = "https://example.com/image.jpg")
        String imageUrl,

        @Schema(description = "Timestamp when the post was created", example = "2025-10-07T10:00:00.000+00:00")
        Date createdAt,

        @Schema(description = "Timestamp when the post was last updated", example = "2025-10-07T11:30:00.000+00:00")
        Date updatedAt
) {
}
