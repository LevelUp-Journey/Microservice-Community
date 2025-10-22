package com.levelup.journey.platform.moderation.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Resource for creating a new content report
 */
@Schema(description = "Request to create a new content report")
public record CreateReportResource(
    @Schema(description = "ID of the post being reported", 
            example = "456e7890-e89b-12d3-a456-426614174001")
    @JsonProperty("postId")
    @NotNull(message = "Post ID is required")
    UUID postId,
    
    @Schema(description = "ID of the user who was reported", 
            example = "012e3456-e89b-12d3-a456-426614174003")
    @JsonProperty("reportedUserId")
    @NotNull(message = "Reported user ID is required")
    UUID reportedUserId,
    
    @Schema(description = "Category of the report", 
            example = "HARASSMENT",
            allowableValues = {
                "HARASSMENT", "HATE_SPEECH", "SPAM", "INAPPROPRIATE", 
                "VIOLENCE", "SEXUAL_CONTENT", "SELF_HARM", "MISINFORMATION", 
                "COPYRIGHT", "RACISM", "DISCRIMINATION", "OTHER"
            })
    @JsonProperty("category")
    @NotBlank(message = "Category is required")
    String category,
    
    @Schema(description = "Description or reason for the report", 
            example = "This user posted inappropriate content that violates community guidelines")
    @JsonProperty("description")
    @NotBlank(message = "Description is required")
    String description
) {}