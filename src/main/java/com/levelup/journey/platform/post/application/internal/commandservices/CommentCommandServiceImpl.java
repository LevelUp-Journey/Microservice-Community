package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.repositories.CommentRepository;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.services.CommentCommandService;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Comment Command Service Implementation
 * Handles comment-related commands
 */
@Service
public class CommentCommandServiceImpl implements CommentCommandService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentCommandServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public Optional<Comment> handle(AddCommentCommand command) {
        // Verify post exists
        var post = postRepository.findById(command.postId());
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + command.postId().value());
        }

        // Create and save the comment
        var commentId = CommentId.random();
        var imageUrl = command.imageUrl() != null ? ImageUrl.of(command.imageUrl()) : ImageUrl.empty();
        var comment = new Comment(
                commentId,
                command.authorId(),
                command.authorProfileId(),
                command.content(),
                imageUrl,
                null // createdAt will be set to Instant.now() by constructor
        );

        commentRepository.save(comment, command.postId());

        return Optional.of(comment);
    }
}
