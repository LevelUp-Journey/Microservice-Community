package com.levelup.journey.platform.moderation.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Report resource for REST API responses
 */
@Schema(description = "Content report information")
public record ReportResource(
    @Schema(description = "Unique report identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("id")
    UUID id,
    
    @Schema(description = "Post that was reported", example = "456e7890-e89b-12d3-a456-426614174001")
    @JsonProperty("postId")
    UUID postId,
    
    @Schema(description = "User who made the report (null for auto-detected reports)", 
            example = "789e0123-e89b-12d3-a456-426614174002")
    @JsonProperty("reporterId")
    UUID reporterId,
    
    @Schema(description = "User who was reported", example = "012e3456-e89b-12d3-a456-426614174003")
    @JsonProperty("reportedUserId")
    UUID reportedUserId,
    
    @Schema(description = "Report category", example = "HARASSMENT")
    @JsonProperty("category")
    String category,
    
    @Schema(description = "Report severity level", example = "HIGH")
    @JsonProperty("severity")
    String severity,
    
    @Schema(description = "Report description or reason", example = "User posted inappropriate content")
    @JsonProperty("description")
    String description,
    
    @Schema(description = "Current report status", example = "PENDING")
    @JsonProperty("status")
    String status,
    
    @Schema(description = "Whether this report was automatically detected", example = "false")
    @JsonProperty("autoDetected")
    boolean autoDetected,
    
    @Schema(description = "Suspicious words found (for auto-detected reports)", 
            example = "[\"spam\", \"inappropriate\"]")
    @JsonProperty("suspiciousWords")
    List<String> suspiciousWords,
    
    @Schema(description = "When the report was created", example = "2024-01-15T10:30:00")
    @JsonProperty("createdAt")
    LocalDateTime createdAt,
    
    @Schema(description = "When the report was last updated", example = "2024-01-15T14:45:00")
    @JsonProperty("updatedAt")
    LocalDateTime updatedAt,
    
    @Schema(description = "Actions performed on this report")
    @JsonProperty("actions")
    List<ReportActionResource> actions
) {
    /**
     * Report action resource
     */
    @Schema(description = "Action performed on a report")
    public record ReportActionResource(
        @Schema(description = "Type of action performed", example = "REVIEWED")
        @JsonProperty("actionType")
        String actionType,
        
        @Schema(description = "Reason for the action", example = "Report reviewed by moderator")
        @JsonProperty("reason")
        String reason,
        
        @Schema(description = "User who performed the action (null for system actions)", 
                example = "345e6789-e89b-12d3-a456-426614174004")
        @JsonProperty("performedBy")
        UUID performedBy,
        
        @Schema(description = "When the action was performed", example = "2024-01-15T11:00:00")
        @JsonProperty("performedAt")
        LocalDateTime performedAt
    ) {}
}