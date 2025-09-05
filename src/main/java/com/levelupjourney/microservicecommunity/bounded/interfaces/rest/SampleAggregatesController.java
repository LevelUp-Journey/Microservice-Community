package com.levelupjourney.microservicecommunity.bounded.interfaces.rest;

import com.levelupjourney.microservicecommunity.bounded.application.internal.commandservices.SampleAggregateCommandService;
import com.levelupjourney.microservicecommunity.bounded.application.internal.queryservices.SampleAggregateQueryService;
import com.levelupjourney.microservicecommunity.bounded.domain.model.aggregates.SampleAggregateRoot;
import com.levelupjourney.microservicecommunity.bounded.domain.model.commands.CreateSampleAggregateCommand;
import com.levelupjourney.microservicecommunity.bounded.domain.model.queries.GetSampleAggregateByBusinessIdQuery;
import com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources.CreateSampleAggregateResource;
import com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources.SampleAggregateResource;
import com.levelupjourney.microservicecommunity.bounded.interfaces.rest.resources.UpdateSampleAggregateResource;
import com.levelupjourney.microservicecommunity.bounded.interfaces.rest.transform.CreateSampleAggregateCommandFromResourceAssembler;
import com.levelupjourney.microservicecommunity.bounded.interfaces.rest.transform.SampleAggregateResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Sample Aggregate operations.
 * Implements the interface layer of DDD, exposing domain functionality via HTTP.
 * 
 * This demonstrates:
 * - RESTful API design
 * - Command and Query separation
 * - Resource transformation
 * - Proper HTTP status codes
 * - API documentation
 */
@RestController
@RequestMapping(value = "/api/v1/sample-aggregates", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sample Aggregates", description = "Operations related to Sample Aggregates")
public class SampleAggregatesController {

    private final SampleAggregateCommandService commandService;
    private final SampleAggregateQueryService queryService;

    public SampleAggregatesController(
            SampleAggregateCommandService commandService,
            SampleAggregateQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Creates a new sample aggregate.
     */
    @PostMapping
    @Operation(summary = "Create a new sample aggregate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Aggregate created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Aggregate with business ID already exists")
    })
    public ResponseEntity<SampleAggregateResource> createSampleAggregate(
            @RequestBody CreateSampleAggregateResource resource) {
        
        try {
            // Transform resource to command
            CreateSampleAggregateCommand command = 
                CreateSampleAggregateCommandFromResourceAssembler.toCommandFromResource(resource);
            
            // Execute command
            String aggregateId = commandService.handle(command);
            
            // Retrieve created aggregate for response
            Optional<SampleAggregateRoot> optionalAggregate = 
                queryService.handle(new GetSampleAggregateByBusinessIdQuery(resource.businessId()));
            
            if (optionalAggregate.isPresent()) {
                SampleAggregateResource responseResource = 
                    SampleAggregateResourceFromEntityAssembler.toResourceFromEntity(optionalAggregate.get());
                
                return ResponseEntity.created(URI.create("/api/v1/sample-aggregates/" + aggregateId))
                                   .body(responseResource);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).build();
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves a sample aggregate by business ID.
     */
    @GetMapping("/{businessId}")
    @Operation(summary = "Get sample aggregate by business ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Aggregate found"),
        @ApiResponse(responseCode = "404", description = "Aggregate not found")
    })
    public ResponseEntity<SampleAggregateResource> getSampleAggregateByBusinessId(
            @Parameter(description = "Business ID of the aggregate")
            @PathVariable String businessId) {
        
        GetSampleAggregateByBusinessIdQuery query = new GetSampleAggregateByBusinessIdQuery(businessId);
        Optional<SampleAggregateRoot> optionalAggregate = queryService.handle(query);
        
        return optionalAggregate
            .map(aggregate -> {
                SampleAggregateResource resource = 
                    SampleAggregateResourceFromEntityAssembler.toResourceFromEntity(aggregate);
                return ResponseEntity.ok(resource);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all active sample aggregates.
     */
    @GetMapping("/active")
    @Operation(summary = "Get all active sample aggregates")
    @ApiResponse(responseCode = "200", description = "Active aggregates retrieved")
    public ResponseEntity<List<SampleAggregateResource>> getActiveSampleAggregates() {
        
        List<SampleAggregateRoot> aggregates = queryService.getAllActiveAggregates();
        List<SampleAggregateResource> resources = aggregates.stream()
            .map(SampleAggregateResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
        
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves sample aggregates with pagination.
     */
    @GetMapping
    @Operation(summary = "Get sample aggregates with pagination")
    @ApiResponse(responseCode = "200", description = "Aggregates retrieved")
    public ResponseEntity<Page<SampleAggregateResource>> getSampleAggregates(
            @Parameter(description = "Pagination information") Pageable pageable) {
        
        Page<SampleAggregateRoot> aggregatesPage = queryService.getAllAggregates(pageable);
        Page<SampleAggregateResource> resourcesPage = aggregatesPage
            .map(SampleAggregateResourceFromEntityAssembler::toResourceFromEntity);
        
        return ResponseEntity.ok(resourcesPage);
    }

    /**
     * Updates an existing sample aggregate.
     */
    @PutMapping("/{businessId}")
    @Operation(summary = "Update sample aggregate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Aggregate updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Aggregate not found")
    })
    public ResponseEntity<SampleAggregateResource> updateSampleAggregate(
            @Parameter(description = "Business ID of the aggregate")
            @PathVariable String businessId,
            @RequestBody UpdateSampleAggregateResource resource) {
        
        try {
            // Execute update command
            commandService.updateAggregate(businessId, resource.name(), resource.description());
            
            // Retrieve updated aggregate for response
            Optional<SampleAggregateRoot> optionalAggregate = 
                queryService.handle(new GetSampleAggregateByBusinessIdQuery(businessId));
            
            return optionalAggregate
                .map(aggregate -> {
                    SampleAggregateResource responseResource = 
                        SampleAggregateResourceFromEntityAssembler.toResourceFromEntity(aggregate);
                    return ResponseEntity.ok(responseResource);
                })
                .orElse(ResponseEntity.notFound().build());
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deactivates a sample aggregate.
     */
    @DeleteMapping("/{businessId}")
    @Operation(summary = "Deactivate sample aggregate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Aggregate deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Aggregate not found")
    })
    public ResponseEntity<Void> deactivateSampleAggregate(
            @Parameter(description = "Business ID of the aggregate")
            @PathVariable String businessId) {
        
        try {
            commandService.deactivateAggregate(businessId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Checks if an aggregate exists.
     */
    @GetMapping("/{businessId}/exists")
    @Operation(summary = "Check if aggregate exists")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    public ResponseEntity<Boolean> checkAggregateExists(
            @Parameter(description = "Business ID of the aggregate")
            @PathVariable String businessId) {
        
        boolean exists = queryService.existsByBusinessId(businessId);
        return ResponseEntity.ok(exists);
    }
}
