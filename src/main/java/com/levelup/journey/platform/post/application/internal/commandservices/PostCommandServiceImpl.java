package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.PublishPostCommand;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.services.PostCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Post Command Service Implementation
 * Handles commands for Post aggregate
 */
@Service
public class PostCommandServiceImpl implements PostCommandService {

    private static final Logger logger = LoggerFactory.getLogger(PostCommandServiceImpl.class);

    private final PostRepository postRepository;

    public PostCommandServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Optional<Post> handle(PublishPostCommand command) {
        logger.info("Processing PublishPostCommand for post ID: {}, title: {}, communityId: {}, authorId: {}",
                   command.id(), command.title(), command.communityId(), command.authorId());

        try {
            // Check if post with this ID already exists
            var existingPost = postRepository.findById(command.id());
            if (existingPost.isPresent()) {
                logger.warn("Attempted to create post with existing ID: {}", command.id());
                throw new IllegalArgumentException("Ya existe un post con el ID: " + command.id());
            }

            // Create new post from command
            Post post = Post.publish(
                    command.id(),
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
            logger.error("Validation error in PublishPostCommand for ID: {} - {}", command.id(), e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing PublishPostCommand for ID: {}", command.id(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }

    @Override
    public Optional<Post> handle(AddCommentCommand command) {
        logger.info("Processing AddCommentCommand for comment ID: {} on post: {}, authorId: {}",
                   command.commentId(), command.postId(), command.authorId());

        try {
            // Find existing post
            Optional<Post> postOptional = postRepository.findById(command.postId());

            if (postOptional.isEmpty()) {
                logger.warn("Post not found with ID: {} for adding comment", command.postId());
                return Optional.empty();
            }

            Post post = postOptional.get();

            // Check if comment with this ID already exists in the post
            boolean commentExists = post.comments().stream()
                    .anyMatch(comment -> comment.id().equals(command.commentId()));

            if (commentExists) {
                logger.warn("Attempted to add comment with existing ID: {} to post: {}", command.commentId(), command.postId());
                throw new IllegalArgumentException("Ya existe un comentario con el ID: " + command.commentId() + " en este post");
            }

            // Add comment to post
            post.addComment(
                    command.commentId(),
                    command.authorId(),
                    command.content()
            );

            // Save updated post
            Post savedPost = postRepository.save(post);
            logger.info("Comment added successfully with ID: {} to post: {}", command.commentId(), command.postId());

            return Optional.of(savedPost);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in AddCommentCommand for comment ID: {} on post: {} - {}",
                        command.commentId(), command.postId(), e.getMessage());
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.error("Unexpected error processing AddCommentCommand for comment ID: {} on post: {}",
                        command.commentId(), command.postId(), e);
            return Optional.empty(); // Return empty for unexpected errors
        }
    }
}
