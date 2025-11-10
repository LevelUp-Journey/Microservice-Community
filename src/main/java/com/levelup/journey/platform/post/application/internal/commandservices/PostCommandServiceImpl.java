package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.commands.*;
import com.levelup.journey.platform.post.domain.model.queries.GetCommentsByPostIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.services.*;
import com.levelup.journey.platform.shared.domain.acl.SocialRelationshipService;
import com.levelup.journey.platform.shared.domain.acl.UserDirectoryService;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final SocialRelationshipService socialRelationshipService;
    private final UserDirectoryService userDirectoryService;

    public PostCommandServiceImpl(PostRepository postRepository,
                                  CommunityQueryService communityQueryService,
                                  CommentCommandService commentCommandService,
                                  CommentQueryService commentQueryService,
                                  SocialRelationshipService socialRelationshipService,
                                  UserDirectoryService userDirectoryService) {
        this.postRepository = postRepository;
        this.communityQueryService = communityQueryService;
        this.commentCommandService = commentCommandService;
        this.commentQueryService = commentQueryService;
        this.socialRelationshipService = socialRelationshipService;
        this.userDirectoryService = userDirectoryService;
    }

    @Override
    public Optional<Post> handle(PublishPostCommand command) {
        // Generate a new UUID for the post
        PostId postId = PostId.of(UUID.randomUUID().toString());

        logger.info("Processing PublishPostCommand for generated post ID: {}, title: {}, communityId: {}, authorId: {}",
                   postId.value(), command.title(), command.communityId(), command.authorId());

        try {
            ProfileId authorProfileId = resolveProfileId(command.authorId());

            // Validate that the community exists
            var communityQuery = new GetCommunityByIdQuery(command.communityId());
            var communityOptional = communityQueryService.handle(communityQuery);
            if (communityOptional.isEmpty()) {
                logger.warn("Attempted to create post for non-existent community ID: {}", command.communityId());
                throw new IllegalArgumentException("La comunidad con ID: " + command.communityId() + " no existe");
            }

            var community = communityOptional.get();

            // Check posting permissions
            boolean canPost = false;

            // Check if user is the community owner
            if (community.ownerId().equals(command.authorId())) {
                canPost = true;
                logger.debug("User {} is community owner, allowing post creation", command.authorId());
            } else {
                // Check if user is a teacher who is subscribed to the community
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getAuthorities() != null) {
                    boolean isTeacher = authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_TEACHER".equals(authority.getAuthority()));

                    if (isTeacher) {
                        // Check if teacher is subscribed to this community
                        boolean isSubscribed = socialRelationshipService.isSubscribedToCommunity(
                            command.authorId().value(), command.communityId().value());

                        if (isSubscribed) {
                            canPost = true;
                            logger.debug("User {} is subscribed teacher, allowing post creation", command.authorId());
                        } else {
                            logger.warn("Teacher {} attempted to post in community {} but is not subscribed",
                                command.authorId(), command.communityId());
                        }
                    } else {
                        logger.warn("User {} attempted to post in community {} but lacks permission (not owner or subscribed teacher)",
                            command.authorId(), command.communityId());
                    }
                }
            }

            if (!canPost) {
                throw new IllegalArgumentException("No tienes permiso para publicar en esta comunidad. Solo el propietario o profesores suscritos pueden publicar.");
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
                    authorProfileId,
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
                    post.authorProfileId(),
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

    @Override
    public Optional<Post> handle(DeleteCommentCommand command) {
        logger.info("Processing DeleteCommentCommand on post: {}, comment: {}, requesterId: {}",
                   command.postId(), command.commentId(), command.requesterId());

        try {
            // Delegate to CommentCommandService to handle comment deletion
            boolean deleted = commentCommandService.handle(command);

            if (!deleted) {
                logger.warn("Failed to delete comment: {} from post: {}", command.commentId(), command.postId());
                return Optional.empty();
            }

            logger.info("Comment deleted successfully: {} from post: {}", command.commentId(), command.postId());

            // Load and return the post with updated comments
            Optional<Post> postOptional = postRepository.findById(command.postId());

            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                // Load comments separately
                var comments = commentQueryService.handle(new GetCommentsByPostIdQuery(command.postId()));

                // Restore post with updated comments
                Post postWithComments = Post.restore(
                    post.id(),
                    post.communityId(),
                    post.authorId(),
                    post.authorProfileId(),
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
            logger.error("Authorization error in DeleteCommentCommand on post: {} - {}",
                        command.postId(), e.getMessage());
            throw e; // Re-throw authorization errors
        } catch (Exception e) {
            logger.error("Unexpected error processing DeleteCommentCommand on post: {}",
                        command.postId(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }

    @Override
    public Optional<Post> handle(EditCommentCommand command) {
        logger.info("Processing EditCommentCommand on post: {}, comment: {}, requesterId: {}",
                   command.postId(), command.commentId(), command.requesterId());

        try {
            // Delegate to CommentCommandService to handle comment editing
            var updatedCommentOptional = commentCommandService.handle(command);

            if (updatedCommentOptional.isEmpty()) {
                logger.warn("Failed to edit comment: {} from post: {}", command.commentId(), command.postId());
                return Optional.empty();
            }

            logger.info("Comment edited successfully: {} from post: {}", command.commentId(), command.postId());

            // Load and return the post with updated comments
            Optional<Post> postOptional = postRepository.findById(command.postId());

            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                // Load comments separately
                var comments = commentQueryService.handle(new GetCommentsByPostIdQuery(command.postId()));

                // Restore post with updated comments
                Post postWithComments = Post.restore(
                    post.id(),
                    post.communityId(),
                    post.authorId(),
                    post.authorProfileId(),
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
            logger.error("Authorization error in EditCommentCommand on post: {} - {}",
                        command.postId(), e.getMessage());
            throw e; // Re-throw authorization errors
        } catch (Exception e) {
            logger.error("Unexpected error processing EditCommentCommand on post: {}",
                        command.postId(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }

    @Override
    public boolean handle(DeletePostCommand command) {
        logger.info("Processing DeletePostCommand for post: {}, requesterId: {}",
                   command.postId(), command.requesterId());

        try {
            ensureUserExists(command.requesterId());

            // Find the post
            Optional<Post> postOptional = postRepository.findById(command.postId());
            if (postOptional.isEmpty()) {
                logger.warn("Post not found for deletion: {}", command.postId());
                return false;
            }

            Post post = postOptional.get();

            // Check authorization
            boolean isAuthorized = false;

            // Check if requester is the post author
            if (post.authorId().equals(command.requesterId())) {
                isAuthorized = true;
                logger.debug("User {} is post author, allowing deletion", command.requesterId());
            } else {
                // Check if requester is the community owner
                var communityQuery = new GetCommunityByIdQuery(post.communityId());
                var communityOptional = communityQueryService.handle(communityQuery);

                if (communityOptional.isPresent()) {
                    var community = communityOptional.get();
                    if (community.ownerId().equals(command.requesterId())) {
                        isAuthorized = true;
                        logger.debug("User {} is community owner, allowing deletion", command.requesterId());
                    }
                }

                // Check if requester is an admin
                if (!isAuthorized) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.getAuthorities() != null) {
                        boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
                        if (isAdmin) {
                            isAuthorized = true;
                            logger.debug("User {} is admin, allowing deletion", command.requesterId());
                        }
                    }
                }
            }

            if (!isAuthorized) {
                logger.warn("User {} attempted to delete post {} but lacks permission", 
                           command.requesterId(), command.postId());
                throw new IllegalArgumentException("No tienes permiso para eliminar este post. Solo el autor, el propietario de la comunidad o un administrador pueden eliminar posts.");
            }

            // Delete the post
            postRepository.deleteById(command.postId());
            logger.info("Post deleted successfully: {}", command.postId());

            return true;

        } catch (IllegalArgumentException e) {
            logger.error("Authorization error in DeletePostCommand for post: {} - {}",
                        command.postId(), e.getMessage());
            throw e; // Re-throw authorization errors
        } catch (Exception e) {
            logger.error("Unexpected error processing DeletePostCommand for post: {}",
                        command.postId(), e);
            return false; // Return false for unexpected errors
        }
    }

    private void ensureUserExists(UserId userId) {
        if (!userDirectoryService.userExists(userId.value())) {
            throw new IllegalArgumentException("El usuario autenticado no existe en el directorio de usuarios.");
        }
    }

    private ProfileId resolveProfileId(UserId userId) {
        String profileId = userDirectoryService.requireProfileIdByUserId(userId.value());
        return ProfileId.of(profileId);
    }
}
