package com.levelupjourney.microservicecommunity.shared.application.acl.iam.impl;

import com.levelupjourney.microservicecommunity.shared.application.acl.iam.IamProfileGateway;
import com.levelupjourney.microservicecommunity.shared.application.acl.iam.ProfileCacheService;
import com.levelupjourney.microservicecommunity.shared.application.acl.iam.ProfileSnapshot;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import com.levelupjourney.microservicecommunity.shared.infrastructure.persistence.mongodb.entities.ProfileCacheDocument;
import com.levelupjourney.microservicecommunity.shared.infrastructure.persistence.mongodb.repositories.ProfileCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of ProfileCacheService.
 * Manages local caching of user profiles from the external IAM service.
 */
@Service
public class ProfileCacheServiceImpl implements ProfileCacheService {
    
    private final ProfileCacheRepository profileCacheRepository;
    private final IamProfileGateway iamProfileGateway;
    
    @Value("${app.profile-cache.stale-after-minutes:60}")
    private int staleAfterMinutes;
    
    public ProfileCacheServiceImpl(
        ProfileCacheRepository profileCacheRepository,
        IamProfileGateway iamProfileGateway
    ) {
        this.profileCacheRepository = profileCacheRepository;
        this.iamProfileGateway = iamProfileGateway;
    }
    
    @Override
    public Optional<ProfileSnapshot> getProfile(UserId userId) {
        // Check cache first
        var cachedProfile = profileCacheRepository.findById(userId.value());
        
        if (cachedProfile.isPresent()) {
            var document = cachedProfile.get();
            
            // If not stale, return cached version
            if (!document.isStale(staleAfterMinutes)) {
                return Optional.of(documentToSnapshot(document));
            }
            
            // If stale, try to refresh
            var refreshed = refreshProfileFromIam(userId);
            if (refreshed.isPresent()) {
                return refreshed;
            }
            
            // If refresh failed, return stale data as fallback
            return Optional.of(documentToSnapshot(document));
        }
        
        // Not in cache, fetch from IAM
        return refreshProfileFromIam(userId);
    }
    
    @Override
    public Optional<ProfileSnapshot> refreshProfile(UserId userId) {
        return refreshProfileFromIam(userId);
    }
    
    @Override
    public void evictProfile(UserId userId) {
        profileCacheRepository.deleteById(userId.value());
    }
    
    @Override
    public boolean validateUser(UserId userId) {
        // Check cache first for quick validation
        if (profileCacheRepository.existsById(userId.value())) {
            return true;
        }
        
        // Check with IAM service
        return iamProfileGateway.userExists(userId);
    }
    
    private Optional<ProfileSnapshot> refreshProfileFromIam(UserId userId) {
        try {
            var profileFromIam = iamProfileGateway.fetchProfile(userId);
            
            if (profileFromIam.isPresent()) {
                var profile = profileFromIam.get();
                
                // Update cache
                var existingDocument = profileCacheRepository.findById(userId.value());
                
                if (existingDocument.isPresent()) {
                    var document = existingDocument.get();
                    document.updateProfile(
                        profile.username(),
                        profile.name(),
                        profile.avatarUrl(),
                        profile.roles()
                    );
                    profileCacheRepository.save(document);
                } else {
                    var newDocument = new ProfileCacheDocument(
                        userId.value(),
                        profile.username(),
                        profile.name(),
                        profile.avatarUrl(),
                        profile.roles(),
                        LocalDateTime.now()
                    );
                    profileCacheRepository.save(newDocument);
                }
                
                return Optional.of(profile);
            }
        } catch (Exception e) {
            // Log error but don't propagate - we'll return empty
            System.err.println("Failed to refresh profile from IAM for user " + userId + ": " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    private ProfileSnapshot documentToSnapshot(ProfileCacheDocument document) {
        return new ProfileSnapshot(
            UserId.of(document.getId()),
            document.getUsername(),
            document.getName(),
            document.getAvatarUrl(),
            document.getRoles(),
            document.getLastSyncedAt()
        );
    }
}
