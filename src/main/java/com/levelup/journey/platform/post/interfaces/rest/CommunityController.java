package com.levelup.journey.platform.post.interfaces.rest;

import com.levelup.journey.platform.post.domain.model.queries.GetAllCommunitiesQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.services.CommunityCommandService;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import com.levelup.journey.platform.post.interfaces.rest.resources.CommunityResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.CreateCommunityResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.UpdateCommunityResource;
import com.levelup.journey.platform.post.interfaces.rest.transform.CommunityResourceFromEntityAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.CreateCommunityCommandFromResourceAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.UpdateCommunityCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Community Controller
 * REST API for managing communities
 */
@RestController
@RequestMapping(value = "/api/v1/communities", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Communities", description = "Operations related to community management")
public class CommunityController {

    private static final Logger logger = LoggerFactory.getLogger(CommunityController.class);

    private final CommunityCommandService communityCommandService;
    private final CommunityQueryService communityQueryService;

    public CommunityController(CommunityCommandService communityCommandService,
                              CommunityQueryService communityQueryService) {
        this.communityCommandService = communityCommandService;
        this.communityQueryService = communityQueryService;
    }

    /**
     * Create a new community
     */
    @PostMapping
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @Operation(summary = "Create a new community", description = "Create a new community with a name and description. Only teachers and admins can create communities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Community created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommunityResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or community already exists",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - only teachers and admins can create communities",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Community with this ID already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<CommunityResource> createCommunity(@Valid @RequestBody CreateCommunityResource resource) {
        logger.info("Creating community with name: {}, ownerId: {}",
                   resource.name(), resource.ownerId());

        try {
            var command = CreateCommunityCommandFromResourceAssembler.toCommandFromResource(resource);
            var community = communityCommandService.handle(command);

            if (community.isEmpty()) {
                logger.warn("Failed to create community with name: {} - service returned empty result", resource.name());
                return ResponseEntity.badRequest().build();
            }

            var communityResource = CommunityResourceFromEntityAssembler.toResourceFromEntity(community.get());
            logger.info("Community created successfully with ID: {}", communityResource.id());
            return new ResponseEntity<>(communityResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating community with name: {} - {}", resource.name(), e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error creating community with name: {}", resource.name(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing community
     */
    @PutMapping("/{communityId}")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @Operation(summary = "Update community", description = "Update an existing community's name, description, and image URL. Only teachers and admins can update communities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommunityResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or community ID format",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - only teachers and admins can update communities",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Community not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<CommunityResource> updateCommunity(
            @PathVariable String communityId,
            @Valid @RequestBody UpdateCommunityResource resource) {
        logger.info("Updating community with ID: {}, name: {}", communityId, resource.name());

        try {
            var command = UpdateCommunityCommandFromResourceAssembler.toCommandFromResource(communityId, resource);
            var community = communityCommandService.handle(command);

            if (community.isEmpty()) {
                logger.warn("Failed to update community with ID: {} - service returned empty result", communityId);
                return ResponseEntity.notFound().build();
            }

            var communityResource = CommunityResourceFromEntityAssembler.toResourceFromEntity(community.get());
            logger.info("Community updated successfully with ID: {}", communityResource.id());
            return ResponseEntity.ok(communityResource);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating community with ID: {} - {}", communityId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error updating community with ID: {}", communityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get community by ID
     */
    @GetMapping("/{communityId}")
    @Operation(summary = "Get community by ID", description = "Retrieve a specific community by its identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommunityResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid community ID format",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Community not found",
                    content = @Content)
    })
    public ResponseEntity<CommunityResource> getCommunityById(@PathVariable String communityId) {
        logger.info("Retrieving community with ID: {}", communityId);

        try {
            var query = new GetCommunityByIdQuery(CommunityId.of(communityId));
            var community = communityQueryService.handle(query);

            if (community.isEmpty()) {
                logger.warn("Community not found with ID: {}", communityId);
                return ResponseEntity.notFound().build();
            }

            var communityResource = CommunityResourceFromEntityAssembler.toResourceFromEntity(community.get());
            logger.debug("Community retrieved successfully with ID: {}", communityId);
            return ResponseEntity.ok(communityResource);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid community ID format: {} - {}", communityId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving community with ID: {}", communityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all communities
     */
    @GetMapping
    @Operation(summary = "Get all communities", description = "Retrieve all existing communities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Communities retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommunityResource.class)))
    })
    public ResponseEntity<List<CommunityResource>> getAllCommunities() {
        logger.info("Retrieving all communities");

        try {
            var query = new GetAllCommunitiesQuery();
            var communities = communityQueryService.handle(query);

            var communityResources = communities.stream()
                    .map(CommunityResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} communities successfully", communityResources.size());
            return ResponseEntity.ok(communityResources);

        } catch (Exception e) {
            logger.error("Unexpected error retrieving all communities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
