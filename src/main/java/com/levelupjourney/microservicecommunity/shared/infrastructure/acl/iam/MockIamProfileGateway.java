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
        // Initialize with some mock users
        addMockUser("user-1", "john_doe", "John Doe", "https://api.dicebear.com/7.x/avataaars/svg?seed=john", List.of("STUDENT"));
        addMockUser("user-2", "jane_smith", "Jane Smith", "https://api.dicebear.com/7.x/avataaars/svg?seed=jane", List.of("TEACHER"));
        addMockUser("user-3", "admin_user", "Admin User", "https://api.dicebear.com/7.x/avataaars/svg?seed=admin", List.of("ADMIN", "TEACHER"));
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
    
    private record MockUser(
        String username,
        String name,
        String avatarUrl,
        List<String> roles
    ) {}
}
