package com.levelup.journey.platform.post.interfaces.rest;

import com.levelup.journey.platform.post.domain.model.queries.GetAllCommunitiesQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.services.CommunityCommandService;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import com.levelup.journey.platform.post.interfaces.rest.resources.CommunityResource;
import com.levelup.journey.platform.post.interfaces.rest.resources.CreateCommunityResource;
import com.levelup.journey.platform.post.interfaces.rest.transform.CommunityResourceFromEntityAssembler;
import com.levelup.journey.platform.post.interfaces.rest.transform.CreateCommunityCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Create a new community", description = "Create a new community with a name and description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Community created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommunityResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<CommunityResource> createCommunity(@RequestBody CreateCommunityResource resource) {
        var command = CreateCommunityCommandFromResourceAssembler.toCommandFromResource(resource);
        var community = communityCommandService.handle(command);

        if (community.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var communityResource = CommunityResourceFromEntityAssembler.toResourceFromEntity(community.get());
        return new ResponseEntity<>(communityResource, HttpStatus.CREATED);
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
            @ApiResponse(responseCode = "404", description = "Community not found",
                    content = @Content)
    })
    public ResponseEntity<CommunityResource> getCommunityById(@PathVariable String communityId) {
        var query = new GetCommunityByIdQuery(CommunityId.of(communityId));
        var community = communityQueryService.handle(query);

        if (community.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var communityResource = CommunityResourceFromEntityAssembler.toResourceFromEntity(community.get());
        return ResponseEntity.ok(communityResource);
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
        var query = new GetAllCommunitiesQuery();
        var communities = communityQueryService.handle(query);

        var communityResources = communities.stream()
                .map(CommunityResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(communityResources);
    }
}
