package com.levelupjourney.microservicecommunity.shared.interfaces.rest;

import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import com.levelupjourney.microservicecommunity.shared.infrastructure.acl.iam.MockIamProfileGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Operation(summary = "Get mock users", description = "Returns available mock users with full details for testing")
    public ResponseEntity<Map<String, Object>> getMockUsers() {
        var mockUsers = mockIamProfileGateway.getAllMockUsers();
        
        return ResponseEntity.ok(Map.of(
            "mockUsers", mockUsers,
            "count", mockUsers.size(),
            "usage", Map.of(
                "description", "Use these user IDs in X-User-Id header for testing",
                "example", "curl -H \"X-User-Id: user-1\" http://localhost:8080/api/v1/posts"
            ),
            "availableUserIds", mockUsers.keySet()
        ));
    }
    
    @GetMapping("/mock-users/{userId}")
    @ConditionalOnBean(MockIamProfileGateway.class)
    @Operation(summary = "Get specific mock user", description = "Returns details for a specific mock user")
    public ResponseEntity<Map<String, Object>> getMockUser(
            @Parameter(description = "User ID to fetch", example = "user-1")
            @PathVariable String userId) {
        
        try {
            // Try to create UserId - this might fail if userId is not a valid UUID
            UserId userIdObj;
            try {
                userIdObj = new UserId(userId);
            } catch (IllegalArgumentException e) {
                // If it's not a valid UUID, check if it's a direct mock user lookup
                var mockUsers = mockIamProfileGateway.getAllMockUsers();
                var mockUser = mockUsers.get(userId);
                if (mockUser != null) {
                    return ResponseEntity.ok(Map.of(
                        "userId", mockUser.userId(),
                        "username", mockUser.username(),
                        "name", mockUser.name(),
                        "avatarUrl", mockUser.avatarUrl(),
                        "roles", mockUser.roles(),
                        "lastSyncedAt", "N/A (Direct mock lookup)",
                        "exists", true
                    ));
                }
                return ResponseEntity.notFound().build();
            }
            
            var profile = mockIamProfileGateway.fetchProfile(userIdObj);
            
            if (profile.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var profileSnapshot = profile.get();
            return ResponseEntity.ok(Map.of(
                "userId", profileSnapshot.userId().value(),
                "username", profileSnapshot.username(),
                "name", profileSnapshot.name(),
                "avatarUrl", profileSnapshot.avatarUrl(),
                "roles", profileSnapshot.roles(),
                "lastSyncedAt", profileSnapshot.lastSyncedAt(),
                "exists", mockIamProfileGateway.userExists(userIdObj)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid user ID format",
                "message", e.getMessage(),
                "availableUsers", mockIamProfileGateway.getAllMockUsers().keySet()
            ));
        }
    }
}
