package com.levelupjourney.microservicecommunity.shared.interfaces.rest;

import com.levelupjourney.microservicecommunity.shared.infrastructure.acl.iam.MockIamProfileGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Health check and test controller for the Community microservice.
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Health check and testing endpoints")
public class HealthController {
    
    private final MockIamProfileGateway mockIamProfileGateway;
    
    public HealthController(MockIamProfileGateway mockIamProfileGateway) {
        this.mockIamProfileGateway = mockIamProfileGateway;
    }
    
    @GetMapping
    @Operation(summary = "Health check", description = "Returns the health status of the service")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "community-microservice",
            "timestamp", java.time.LocalDateTime.now(),
            "features", List.of(
                "posting",
                "interaction-foundation",
                "iam-acl",
                "profile-cache",
                "outbox-pattern"
            )
        ));
    }
    
    @GetMapping("/mock-users")
    @ConditionalOnBean(MockIamProfileGateway.class)
    @Operation(summary = "Get mock users", description = "Returns available mock users for testing")
    public ResponseEntity<Map<String, Object>> getMockUsers() {
        return ResponseEntity.ok(Map.of(
            "mockUsers", mockIamProfileGateway.getMockUserIds(),
            "note", "Use these user IDs in X-User-Id header for testing"
        ));
    }
}
