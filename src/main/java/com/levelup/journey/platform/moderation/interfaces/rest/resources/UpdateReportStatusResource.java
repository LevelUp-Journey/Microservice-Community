package com.levelup.journey.platform.moderation.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating report status
 */
@Schema(description = "Request to update report status")
public record UpdateReportStatusResource(
    @Schema(description = "New status for the report", 
            example = "APPROVED",
            allowableValues = {"APPROVED", "REJECTED", "RESOLVED", "ESCALATED", "UNDER_REVIEW"})
    @JsonProperty("status")
    @NotBlank(message = "Status is required")
    String status,
    
    @Schema(description = "Reason for the status update", 
            example = "Report has been reviewed and approved by moderator")
    @JsonProperty("reason")
    @NotBlank(message = "Reason is required")
    String reason
) {}