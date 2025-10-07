package com.levelup.journey.platform.community.post.application.internal.commandservices;

import com.levelup.journey.platform.community.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.community.post.domain.model.commands.CreatePostCommand;
import com.levelup.journey.platform.community.post.domain.model.commands.DeletePostCommand;
import com.levelup.journey.platform.community.post.domain.model.commands.UpdatePostCommand;
import com.levelup.journey.platform.community.post.domain.model.valueobjects.Body;
import com.levelup.journey.platform.community.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.community.post.domain.model.valueobjects.Title;
import com.levelup.journey.platform.community.post.domain.services.PostCommandService;
import com.levelup.journey.platform.community.post.infrastructure.persistence.jpa.repositories.PostRepository;
import com.levelup.journey.platform.community.shared.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of PostCommandService.
 * Handles all command operations for posts with business rule enforcement.
 */
@Service
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;

    public PostCommandServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * {@inheritDoc}
     * Creates a new post after validating all business rules.
     * Note: Role validation (DOCENTE) should be performed at the controller/security layer
     * before this method is called, as this service assumes the user is authorized.
     */
    @Override
    @Transactional
    public Optional<Post> handle(CreatePostCommand command) {
        try {
            // Create value objects with validation
            var userId = new UserId(command.userId());
            var title = new Title(command.title());
            var body = new Body(command.body());
            var imageUrl = command.imageUrl() != null && !command.imageUrl().isBlank()
                    ? new ImageUrl(command.imageUrl())
                    : new ImageUrl(null);

            // Create and save the post
            var post = new Post(userId, title, body, imageUrl);
            var savedPost = postRepository.save(post);

            return Optional.of(savedPost);
        } catch (IllegalArgumentException e) {
            // Validation failed
            throw new IllegalArgumentException("Failed to create post: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * Updates an existing post after verifying the user is the author.
     */
    @Override
    @Transactional
    public Optional<Post> handle(UpdatePostCommand command) {
        try {
            // Find the post
            var postOpt = postRepository.findByIdAndIsDeletedFalse(command.postId());
            if (postOpt.isEmpty()) {
                throw new IllegalArgumentException("Post not found with ID: " + command.postId());
            }

            var post = postOpt.get();
            var userId = new UserId(command.userId());

            // Verify authorization: only the author can update
            if (!post.isAuthor(userId)) {
                throw new IllegalArgumentException("User is not authorized to update this post");
            }

            // Create value objects with validation
            var title = new Title(command.title());
            var body = new Body(command.body());
            var imageUrl = command.imageUrl() != null && !command.imageUrl().isBlank()
                    ? new ImageUrl(command.imageUrl())
                    : new ImageUrl(null);

            // Update the post
            post.update(title, body, imageUrl);
            var updatedPost = postRepository.save(post);

            return Optional.of(updatedPost);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("Failed to update post: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * Performs logical deletion after verifying the user is the author.
     */
    @Override
    @Transactional
    public Optional<Post> handle(DeletePostCommand command) {
        try {
            // Find the post
            var postOpt = postRepository.findByIdAndIsDeletedFalse(command.postId());
            if (postOpt.isEmpty()) {
                throw new IllegalArgumentException("Post not found with ID: " + command.postId());
            }

            var post = postOpt.get();
            var userId = new UserId(command.userId());

            // Verify authorization: only the author can delete
            if (!post.isAuthor(userId)) {
                throw new IllegalArgumentException("User is not authorized to delete this post");
            }

            // Perform logical deletion
            post.delete();
            var deletedPost = postRepository.save(post);

            return Optional.of(deletedPost);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("Failed to delete post: " + e.getMessage(), e);
        }
    }
}
