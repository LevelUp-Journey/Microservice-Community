package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.PublishPostCommand;
import com.levelup.journey.platform.post.domain.model.queries.GetCommunityByIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.services.CommunityQueryService;
import com.levelup.journey.platform.post.domain.services.PostCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Post Command Service Implementation
 * Handles commands for Post aggregate
 */
@Service
public class PostCommandServiceImpl implements PostCommandService {

    private static final Logger logger = LoggerFactory.getLogger(PostCommandServiceImpl.class);

    private final PostRepository postRepository;
    private final CommunityQueryService communityQueryService;

    public PostCommandServiceImpl(PostRepository postRepository,
                                  CommunityQueryService communityQueryService) {
        this.postRepository = postRepository;
        this.communityQueryService = communityQueryService;
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
            Post post = Post.publish(
                    postId,
                    command.communityId(),
                    command.authorId(),
                    command.title(),
                    command.content()
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
        // Generate a new UUID for the comment
        CommentId commentId = CommentId.of(UUID.randomUUID().toString());

        logger.info("Processing AddCommentCommand for generated comment ID: {} on post: {}, authorId: {}",
                   commentId.value(), command.postId(), command.authorId());

        try {
            // Find existing post
            Optional<Post> postOptional = postRepository.findById(command.postId());

            if (postOptional.isEmpty()) {
                logger.warn("Post not found with ID: {} for adding comment", command.postId());
                return Optional.empty();
            }

            Post post = postOptional.get();

            // Check if comment with this ID already exists in the post (very unlikely with UUID)
            boolean commentExists = post.comments().stream()
                    .anyMatch(comment -> comment.id().equals(commentId));

            if (commentExists) {
                logger.warn("Attempted to add comment with existing ID: {} to post: {}", commentId, command.postId());
                throw new IllegalArgumentException("Ya existe un comentario con el ID: " + commentId + " en este post");
            }

            // Add comment to post
            post.addComment(
                    commentId,
                    command.authorId(),
                    command.content()
            );

            // Save updated post
            Post savedPost = postRepository.save(post);
            logger.info("Comment added successfully with ID: {} to post: {}", commentId, command.postId());

            return Optional.of(savedPost);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in AddCommentCommand for comment ID: {} on post: {} - {}",
                        commentId, command.postId(), e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing AddCommentCommand for comment ID: {} on post: {}",
                        commentId, command.postId(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }
}
