package com.levelupjourney.microservicecommunity.interaction.application.internal.commandservices;

import com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates.Like;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.LikePostCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.UnlikePostCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.services.LikeCommandService;
import com.levelupjourney.microservicecommunity.interaction.infrastructure.persistence.mongodb.repositories.LikeRepository;
import com.levelupjourney.microservicecommunity.shared.application.acl.iam.ProfileCacheService;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import com.levelupjourney.microservicecommunity.shared.domain.services.AuthorizationService;
import org.springframework.stereotype.Service;

/**
 * Implementation of like command service.
 */
@Service
public class LikeCommandServiceImpl implements LikeCommandService {
    
    private final LikeRepository likeRepository;
    private final ProfileCacheService profileCacheService;
    private final AuthorizationService authorizationService;
    
    public LikeCommandServiceImpl(LikeRepository likeRepository, 
                                ProfileCacheService profileCacheService,
                                AuthorizationService authorizationService) {
        this.likeRepository = likeRepository;
        this.profileCacheService = profileCacheService;
        this.authorizationService = authorizationService;
    }
    
    @Override
    public boolean handle(LikePostCommand command) {
        var userId = UserId.of(command.userId());
        var postId = PostId.of(command.postId());
        
        // Validate user exists and can like posts
        if (!authorizationService.canLikePost(userId)) {
            throw new SecurityException("User is not authorized to like posts");
        }
        
        // Check if user has already liked this post (enforce one like per user per post)
        var existingLike = likeRepository.findByPostIdAndUserId(postId.value(), userId.value());
        if (existingLike.isPresent()) {
            throw new IllegalStateException("User has already liked this post");
        }
        
        // Create and save the like
        var like = Like.create(postId, userId);
        likeRepository.save(like);
        
        return true;
    }
    
    @Override
    public boolean handle(UnlikePostCommand command) {
        var userId = UserId.of(command.userId());
        var postId = PostId.of(command.postId());
        
        // Validate user exists
        if (!profileCacheService.validateUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + command.userId());
        }
        
        // Find the existing like
        var existingLike = likeRepository.findByPostIdAndUserId(postId.value(), userId.value());
        if (existingLike.isEmpty()) {
            throw new IllegalStateException("User has not liked this post");
        }
        
        // Call unlike method on aggregate before removing
        existingLike.get().unlike();
        
        // Remove the like
        likeRepository.delete(existingLike.get());
        return true;
    }
}
