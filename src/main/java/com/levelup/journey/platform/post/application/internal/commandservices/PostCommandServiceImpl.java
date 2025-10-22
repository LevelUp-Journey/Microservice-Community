package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.PublishPostCommand;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetCommentsByPostIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.services.CommentCommandService;
import com.levelup.journey.platform.post.domain.services.CommentQueryService;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import com.levelup.journey.platform.post.domain.services.PostCommandService;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Post Command Service Implementation
 * Handles commands for Post aggregate
 * Comments are now handled by CommentCommandService
 */
@Service
public class PostCommandServiceImpl implements PostCommandService {

    private static final Logger logger = LoggerFactory.getLogger(PostCommandServiceImpl.class);

    private final PostRepository postRepository;
    private final CommunityQueryService communityQueryService;
    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    public PostCommandServiceImpl(PostRepository postRepository,
                                  CommunityQueryService communityQueryService,
                                  CommentCommandService commentCommandService,
                                  CommentQueryService commentQueryService) {
        this.postRepository = postRepository;
        this.communityQueryService = communityQueryService;
        this.commentCommandService = commentCommandService;
        this.commentQueryService = commentQueryService;
    }

    @Override
    public Optional<Post> handle(PublishPostCommand command) {
        // Generate a new UUID for the post
        PostId postId = PostId.of(UUID.randomUUID().toString());

        logger.info("Processing PublishPostCommand for generated post ID: {}, title: {}, communityId: {}, authorId: {}",
                   postId.value(), command.title(), command.communityId(), command.authorId());

        try {
            // Validate that the community exists
            var communityQuery = new GetCommunityByIdQuery(command.communityId());
            var communityOptional = communityQueryService.handle(communityQuery);
            if (communityOptional.isEmpty()) {
                logger.warn("Attempted to create post for non-existent community ID: {}", command.communityId());
                throw new IllegalArgumentException("La comunidad con ID: " + command.communityId() + " no existe");
            }

            // Check if post with this ID already exists (very unlikely with UUID)
            var existingPost = postRepository.findById(postId);
            if (existingPost.isPresent()) {
                logger.warn("Attempted to create post with existing ID: {}", postId);
                throw new IllegalArgumentException("Ya existe un post con el ID: " + postId);
            }

            // Create new post from command
            var imageUrl = command.imageUrl() != null ? ImageUrl.of(command.imageUrl()) : ImageUrl.empty();
            Post post = Post.publish(
                    postId,
                    command.communityId(),
                    command.authorId(),
                    command.title(),
                    command.content(),
                    imageUrl
            );

            // Save post
            Post savedPost = postRepository.save(post);
            logger.info("Post published and saved successfully with ID: {}", savedPost.id());

            return Optional.of(savedPost);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in PublishPostCommand for ID: {} - {}", postId, e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing PublishPostCommand for ID: {}", postId, e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }

    @Override
    public Optional<Post> handle(AddCommentCommand command) {
        logger.info("Processing AddCommentCommand on post: {}, authorId: {}",
                   command.postId(), command.authorId());

        try {
            // Delegate to CommentCommandService to handle comment creation
            var commentOptional = commentCommandService.handle(command);

            if (commentOptional.isEmpty()) {
                logger.warn("Failed to add comment to post: {}", command.postId());
                return Optional.empty();
            }

            logger.info("Comment added successfully with ID: {} to post: {}",
                       commentOptional.get().id().value(), command.postId());

            // Load and return the post with comments
            Optional<Post> postOptional = postRepository.findById(command.postId());

            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                // Load comments separately
                var comments = commentQueryService.handle(new GetCommentsByPostIdQuery(command.postId()));

                // Restore post with loaded comments
                Post postWithComments = Post.restore(
                    post.id(),
                    post.communityId(),
                    post.authorId(),
                    post.title(),
                    post.content(),
                    post.imageUrl(),
                    post.createdAt(),
                    comments
                );

                return Optional.of(postWithComments);
            }

            return Optional.empty();

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in AddCommentCommand on post: {} - {}",
                        command.postId(), e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing AddCommentCommand on post: {}",
                        command.postId(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }
}
