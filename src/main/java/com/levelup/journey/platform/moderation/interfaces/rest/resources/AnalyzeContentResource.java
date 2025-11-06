package com.levelup.journey.platform.moderation.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Resource for analyzing content for suspicious words
 */
@Schema(description = "Request to analyze content for suspicious words")
public record AnalyzeContentResource(
    @Schema(description = "ID of the post to analyze", 
            example = "456e7890-e89b-12d3-a456-426614174001")
    @JsonProperty("postId")
    @NotNull(message = "Post ID is required")
    UUID postId,
    
    @Schema(description = "ID of the user who created the content", 
            example = "012e3456-e89b-12d3-a456-426614174003")
    @JsonProperty("userId")
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @Schema(description = "Content to analyze for suspicious words", 
            example = "This is the content that needs to be analyzed for inappropriate language")
    @JsonProperty("content")
    @NotNull(message = "Content is required")
    String content
) {}