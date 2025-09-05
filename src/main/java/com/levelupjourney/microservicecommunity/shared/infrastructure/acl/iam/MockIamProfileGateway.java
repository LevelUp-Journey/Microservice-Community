package com.levelupjourney.microservicecommunity.shared.infrastructure.acl.iam;

import com.levelupjourney.microservicecommunity.shared.application.acl.iam.IamProfileGateway;
import com.levelupjourney.microservicecommunity.shared.application.acl.iam.ProfileSnapshot;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of IamProfileGateway for development and testing.
 * This should be replaced with a real HTTP client implementation for production.
 */
@Service
@ConditionalOnProperty(name = "app.iam.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockIamProfileGateway implements IamProfileGateway {
    
    // Mock data store
    private final Map<String, MockUser> mockUsers = new ConcurrentHashMap<>();
    
    public MockIamProfileGateway() {
        // Initialize with some mock users using proper UUIDs
        addMockUser("550e8400-e29b-41d4-a716-446655440001", "john_doe", "John Doe", "https://api.dicebear.com/7.x/avataaars/svg?seed=john", List.of("STUDENT"));
        addMockUser("550e8400-e29b-41d4-a716-446655440002", "jane_smith", "Jane Smith", "https://api.dicebear.com/7.x/avataaars/svg?seed=jane", List.of("TEACHER"));
        addMockUser("550e8400-e29b-41d4-a716-446655440003", "bob_wilson", "Bob Wilson", "https://api.dicebear.com/7.x/avataaars/svg?seed=bob", List.of("STUDENT"));
        addMockUser("550e8400-e29b-41d4-a716-446655440004", "admin_user", "Admin User", "https://api.dicebear.com/7.x/avataaars/svg?seed=admin", List.of("ADMIN"));
        
        // Add real user from IAM service
        addMockUser("e057bb73-19fe-4eb7-99ab-7d962cad3ed5", "user691412544", "Mateo Alemán", "https://lh3.googleusercontent.com/a/ACg8ocK2rli3FclWSpceX6rrI5RTrgmzmWkQBNRFf6_505ZzYPGIAqgP=s96-c", List.of("STUDENT"));
        
        // Also add simple keys that map to the same UUIDs for easier testing
        addMockUser("user-1", "john_doe", "John Doe", "https://api.dicebear.com/7.x/avataaars/svg?seed=john", List.of("STUDENT"));
        addMockUser("user-2", "jane_smith", "Jane Smith", "https://api.dicebear.com/7.x/avataaars/svg?seed=jane", List.of("TEACHER"));
        addMockUser("user-3", "bob_wilson", "Bob Wilson", "https://api.dicebear.com/7.x/avataaars/svg?seed=bob", List.of("STUDENT"));
        addMockUser("admin", "admin_user", "Admin User", "https://api.dicebear.com/7.x/avataaars/svg?seed=admin", List.of("ADMIN"));
        addMockUser("mateo", "user691412544", "Mateo Alemán", "https://lh3.googleusercontent.com/a/ACg8ocK2rli3FclWSpceX6rrI5RTrgmzmWkQBNRFf6_505ZzYPGIAqgP=s96-c", List.of("STUDENT"));
    }
    
    @Override
    public Optional<ProfileSnapshot> fetchProfile(UserId userId) {
        var mockUser = mockUsers.get(userId.value());
        
        if (mockUser == null) {
            return Optional.empty();
        }
        
        return Optional.of(new ProfileSnapshot(
            userId,
            mockUser.username,
            mockUser.name,
            mockUser.avatarUrl,
            mockUser.roles,
            LocalDateTime.now()
        ));
    }
    
    @Override
    public boolean userExists(UserId userId) {
        return mockUsers.containsKey(userId.value());
    }
    
    /**
     * Helper method to add mock users for testing.
     */
    public void addMockUser(String userId, String username, String name, String avatarUrl, List<String> roles) {
        mockUsers.put(userId, new MockUser(username, name, avatarUrl, roles));
    }
    
    /**
     * Helper method to remove mock users.
     */
    public void removeMockUser(String userId) {
        mockUsers.remove(userId);
    }
    
    /**
     * Get all mock user IDs for testing.
     */
    public List<String> getMockUserIds() {
        return List.copyOf(mockUsers.keySet());
    }
    
    /**
     * Get all mock users with their details for testing.
     */
    public Map<String, MockUserInfo> getAllMockUsers() {
        return mockUsers.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> new MockUserInfo(
                    entry.getKey(),
                    entry.getValue().username,
                    entry.getValue().name,
                    entry.getValue().avatarUrl,
                    entry.getValue().roles
                )
            ));
    }
    
    /**
     * Public record for exposing mock user information.
     */
    public record MockUserInfo(
        String userId,
        String username,
        String name,
        String avatarUrl,
        List<String> roles
    ) {}
    
    private record MockUser(
        String username,
        String name,
        String avatarUrl,
        List<String> roles
    ) {}
}
