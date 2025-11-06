package com.levelup.journey.platform.moderation.interfaces.rest;

import com.levelup.journey.platform.moderation.domain.model.aggregates.ContentReport;
import com.levelup.journey.platform.moderation.domain.model.queries.*;
import com.levelup.journey.platform.moderation.domain.model.valueobjects.*;
import com.levelup.journey.platform.moderation.domain.services.ReportCommandService;
import com.levelup.journey.platform.moderation.domain.services.ReportQueryService;
import com.levelup.journey.platform.moderation.interfaces.rest.resources.*;
import com.levelup.journey.platform.moderation.interfaces.rest.transform.ReportResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Content Moderation operations
 */
@RestController
@RequestMapping(value = "/api/v1/moderation/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Content Moderation", description = "Operations for managing content reports and moderation")
public class ReportController {

    private final ReportCommandService reportCommandService;
    private final ReportQueryService reportQueryService;

    public ReportController(ReportCommandService reportCommandService,
                           ReportQueryService reportQueryService) {
        this.reportCommandService = reportCommandService;
        this.reportQueryService = reportQueryService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new content report",
        description = "Creates a new manual report for inappropriate content"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Report created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResource.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<ReportResource> createReport(
            @Valid @RequestBody CreateReportResource resource,
            @RequestHeader("User-Id") String userIdHeader) {
        
        // Extract user ID from header
        UserId reporterUserId = new UserId(UUID.fromString(userIdHeader));
        
        // Convert to command
        var command = ReportResourceAssembler.toCommandFromResource(resource, reporterUserId);
        
        // Execute command
        ReportId reportId = reportCommandService.handle(command);
        
        // Get created report
        Optional<ContentReport> report = reportQueryService.handle(new GetReportByIdQuery(reportId));
        
        if (report.isPresent()) {
            ReportResource reportResource = ReportResourceAssembler.toResourceFromAggregate(report.get());
            return new ResponseEntity<>(reportResource, HttpStatus.CREATED);
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/analyze")
    @Operation(
        summary = "Analyze content for suspicious words",
        description = "Analyzes content and creates auto-detected reports if suspicious words are found"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Content analyzed successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResource.class))
    )
    @ApiResponse(responseCode = "204", description = "No suspicious content found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<ReportResource> analyzeContent(@Valid @RequestBody AnalyzeContentResource resource) {
        
        // Convert to command
        var command = ReportResourceAssembler.toCommandFromResource(resource);
        
        // Execute command
        ReportId reportId = reportCommandService.handle(command);
        
        // If no report was created (no suspicious content), return 204
        if (reportId == null) {
            return ResponseEntity.noContent().build();
        }
        
        // Get created report
        Optional<ContentReport> report = reportQueryService.handle(new GetReportByIdQuery(reportId));
        
        if (report.isPresent()) {
            ReportResource reportResource = ReportResourceAssembler.toResourceFromAggregate(report.get());
            return ResponseEntity.ok(reportResource);
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{reportId}")
    @Operation(
        summary = "Get report by ID",
        description = "Retrieves a specific report by its ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Report found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResource.class))
    )
    @ApiResponse(responseCode = "404", description = "Report not found")
    public ResponseEntity<ReportResource> getReport(
            @Parameter(description = "Report ID") @PathVariable UUID reportId) {
        
        Optional<ContentReport> report = reportQueryService.handle(new GetReportByIdQuery(new ReportId(reportId)));
        
        if (report.isPresent()) {
            ReportResource reportResource = ReportResourceAssembler.toResourceFromAggregate(report.get());
            return ResponseEntity.ok(reportResource);
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(
        summary = "Get all reports",
        description = "Retrieves all content reports in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Reports retrieved successfully",
        content = @Content(mediaType = "application/json")
    )
    public ResponseEntity<List<ReportResource>> getAllReports() {
        
        List<ContentReport> reports = reportQueryService.handle(new GetAllReportsQuery());
        
        List<ReportResource> resources = reports.stream()
            .map(ReportResourceAssembler::toResourceFromAggregate)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/post/{postId}")
    @Operation(
        summary = "Get reports for a specific post",
        description = "Retrieves all reports associated with a specific post"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Reports found",
        content = @Content(mediaType = "application/json")
    )
    public ResponseEntity<List<ReportResource>> getReportsByPost(
            @Parameter(description = "Post ID") @PathVariable UUID postId) {
        
        List<ContentReport> reports = reportQueryService.handle(new GetReportsByPostIdQuery(new PostId(postId)));
        
        List<ReportResource> resources = reports.stream()
            .map(ReportResourceAssembler::toResourceFromAggregate)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get reports for a specific user",
        description = "Retrieves all reports where a specific user was reported"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Reports found",
        content = @Content(mediaType = "application/json")
    )
    public ResponseEntity<List<ReportResource>> getReportsByUser(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        
        List<ContentReport> reports = reportQueryService.handle(new GetReportsByUserIdQuery(
            new UserId(userId), null, null, null, null, null));
        
        List<ReportResource> resources = reports.stream()
            .map(ReportResourceAssembler::toResourceFromAggregate)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/category/{category}")
    @Operation(
        summary = "Get reports by category",
        description = "Retrieves all reports of a specific category"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Reports found",
        content = @Content(mediaType = "application/json")
    )
    public ResponseEntity<List<ReportResource>> getReportsByCategory(
            @Parameter(description = "Report category") @PathVariable String category) {
        
        ReportCategory categoryEnum = ReportResourceAssembler.toReportCategory(category);
        
        List<ContentReport> reports = reportQueryService.handle(new GetReportsByCategoryQuery(
            categoryEnum, null, null, null, null, null));
        
        List<ReportResource> resources = reports.stream()
            .map(ReportResourceAssembler::toResourceFromAggregate)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{reportId}/status")
    @Operation(
        summary = "Update report status",
        description = "Updates the status of a specific report"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Status updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResource.class))
    )
    @ApiResponse(responseCode = "404", description = "Report not found")
    @ApiResponse(responseCode = "400", description = "Invalid status or request data")
    public ResponseEntity<ReportResource> updateReportStatus(
            @Parameter(description = "Report ID") @PathVariable UUID reportId,
            @Valid @RequestBody UpdateReportStatusResource resource,
            @RequestHeader("X-User-Id") String userIdHeader) {
        
        // Extract moderator ID from header
        UserId moderatorId = new UserId(UUID.fromString(userIdHeader));
        ReportId reportIdVO = new ReportId(reportId);
        
        // Execute appropriate command based on status
        ReportStatus newStatus = ReportResourceAssembler.toReportStatus(resource.status());
        
        switch (newStatus) {
            case APPROVED -> reportCommandService.approveReport(reportIdVO, moderatorId, resource.reason());
            case REJECTED -> reportCommandService.rejectReport(reportIdVO, moderatorId, resource.reason());
            case ESCALATED -> reportCommandService.escalateReport(reportIdVO, moderatorId, resource.reason());
            case RESOLVED -> reportCommandService.resolveReport(reportIdVO, moderatorId, resource.reason());
            default -> throw new IllegalArgumentException("Invalid status for update: " + newStatus);
        }
        
        // Get updated report
        Optional<ContentReport> report = reportQueryService.handle(new GetReportByIdQuery(reportIdVO));
        
        if (report.isPresent()) {
            ReportResource reportResource = ReportResourceAssembler.toResourceFromAggregate(report.get());
            return ResponseEntity.ok(reportResource);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{reportId}")
    @Operation(
        summary = "Delete a report",
        description = "Deletes a specific report (admin only)"
    )
    @ApiResponse(responseCode = "204", description = "Report deleted successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "Report ID") @PathVariable UUID reportId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        
        // Extract moderator ID from header
        UserId moderatorId = new UserId(UUID.fromString(userIdHeader));
        ReportId reportIdVO = new ReportId(reportId);
        
        // Check if report exists
        if (!reportQueryService.reportExists(reportIdVO)) {
            return ResponseEntity.notFound().build();
        }
        
        // Delete the report
        reportCommandService.deleteReport(reportIdVO, moderatorId);
        
        return ResponseEntity.noContent().build();
    }


}