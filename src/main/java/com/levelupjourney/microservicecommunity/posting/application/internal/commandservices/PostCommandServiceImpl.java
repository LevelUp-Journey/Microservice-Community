package com.levelupjourney.microservicecommunity.posting.application.internal.commandservices;

import com.levelupjourney.microservicecommunity.posting.domain.model.aggregates.Post;
import com.levelupjourney.microservicecommunity.posting.domain.model.commands.CreatePostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.model.commands.DeletePostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.model.commands.EditPostCommand;
import com.levelupjourney.microservicecommunity.posting.domain.services.PostCommandService;
import com.levelupjourney.microservicecommunity.posting.infrastructure.persistence.mongodb.repositories.PostRepository;
import com.levelupjourney.microservicecommunity.shared.application.acl.iam.ProfileCacheService;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import com.levelupjourney.microservicecommunity.shared.domain.services.AuthorizationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of PostCommandService.
 * Handles post command operations with domain validation and ACL integration.
 */
@Service
public class PostCommandServiceImpl implements PostCommandService {
    
    private final PostRepository postRepository;
    private final ProfileCacheService profileCacheService;
    private final AuthorizationService authorizationService;
    
    public PostCommandServiceImpl(
        PostRepository postRepository,
        ProfileCacheService profileCacheService,
        AuthorizationService authorizationService
    ) {
        this.postRepository = postRepository;
        this.profileCacheService = profileCacheService;
        this.authorizationService = authorizationService;
    }
    
    @Override
    public Optional<PostId> handle(CreatePostCommand command) {
        // Validate user exists through ACL
        var userId = UserId.of(command.authorId());
        if (!profileCacheService.validateUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + command.authorId());
        }
        
        // Create and save the post
        var post = Post.create(userId, command.content());
        postRepository.save(post);
        
        return Optional.of(post.getPostId());
    }
    
    @Override
    public Optional<PostId> handle(EditPostCommand command) {
        var postId = PostId.of(command.postId());
        var editorId = UserId.of(command.editorId());
        
        // Validate user exists through ACL
        if (!profileCacheService.validateUser(editorId)) {
            throw new IllegalArgumentException("User does not exist: " + command.editorId());
        }
        
        // Find and edit the post
        var post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + command.postId()));
        
        post.edit(command.newContent(), editorId);
        postRepository.save(post);
        
        return Optional.of(post.getPostId());
    }
    
    @Override
    public Optional<PostId> handle(DeletePostCommand command) {
        var postId = PostId.of(command.postId());
        var deleterId = UserId.of(command.deleterId());
        
        // Validate user exists through ACL
        if (!profileCacheService.validateUser(deleterId)) {
            throw new IllegalArgumentException("User does not exist: " + command.deleterId());
        }
        
        // Find the post
        var post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + command.postId()));
        
        // Check authorization (post owner or admin)
        if (!authorizationService.canDeletePost(post.getAuthorId(), deleterId)) {
            throw new SecurityException("User is not authorized to delete this post");
        }
        
        post.delete(deleterId);
        postRepository.save(post);
        
        return Optional.of(post.getPostId());
    }
}
