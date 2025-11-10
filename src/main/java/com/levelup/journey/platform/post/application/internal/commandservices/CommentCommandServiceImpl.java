package com.levelup.journey.platform.post.application.internal.commandservices;

import com.levelup.journey.platform.post.domain.model.commands.AddCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.DeleteCommentCommand;
import com.levelup.journey.platform.post.domain.model.commands.EditCommentCommand;
import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.repositories.CommentRepository;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.services.CommentCommandService;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.shared.domain.acl.UserDirectoryService;
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
    private final UserDirectoryService userDirectoryService;

    public CommentCommandServiceImpl(CommentRepository commentRepository,
                                     PostRepository postRepository,
                                     UserDirectoryService userDirectoryService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userDirectoryService = userDirectoryService;
    }

    @Override
    @Transactional
    public Optional<Comment> handle(AddCommentCommand command) {
        // Verify post exists
        var post = postRepository.findById(command.postId());
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + command.postId().value());
        }

        var authorProfileId = resolveProfileId(command.authorId());

        // Create and save the comment
        var commentId = CommentId.random();
        var imageUrl = command.imageUrl() != null ? ImageUrl.of(command.imageUrl()) : ImageUrl.empty();
        var comment = new Comment(
                commentId,
                command.authorId(),
                authorProfileId,
                command.content(),
                imageUrl,
                null // createdAt will be set to Instant.now() by constructor
        );

        commentRepository.save(comment, command.postId());

        return Optional.of(comment);
    }

    @Override
    @Transactional
    public boolean handle(DeleteCommentCommand command) {
        ensureUserExists(command.requesterId());

        // Verify post exists
        var post = postRepository.findById(command.postId());
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + command.postId().value());
        }

        // Find the comment
        var commentOptional = commentRepository.findByIdAndPostId(command.commentId(), command.postId());
        if (commentOptional.isEmpty()) {
            return false;
        }

        var comment = commentOptional.get();

        // Check authorization: only comment author, post owner, or admin can delete
        boolean isAuthorized = false;

        // Check if requester is the comment author
        if (comment.authorId().equals(command.requesterId())) {
            isAuthorized = true;
        } else {
            // Check if requester is the post author
            if (post.get().authorId().equals(command.requesterId())) {
                isAuthorized = true;
            } else {
                // Check if requester is an admin (this would need to be implemented based on your security context)
                // For now, we'll assume admin check is handled at the controller level
                // isAuthorized = checkIfAdmin(command.requesterId());
            }
        }

        if (!isAuthorized) {
            throw new IllegalArgumentException("No tienes permiso para eliminar este comentario. Solo el autor del comentario, el autor del post o un administrador pueden eliminar comentarios.");
        }

        // Delete the comment
        return commentRepository.deleteByIdAndPostId(command.commentId(), command.postId());
    }

    @Override
    @Transactional
    public Optional<Comment> handle(EditCommentCommand command) {
        ensureUserExists(command.requesterId());

        // Verify post exists
        var post = postRepository.findById(command.postId());
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + command.postId().value());
        }

        // Find the comment
        var commentOptional = commentRepository.findByIdAndPostId(command.commentId(), command.postId());
        if (commentOptional.isEmpty()) {
            return Optional.empty();
        }

        var existingComment = commentOptional.get();

        // Check authorization: only comment author can edit
        if (!existingComment.authorId().equals(command.requesterId())) {
            throw new IllegalArgumentException("No tienes permiso para editar este comentario. Solo el autor del comentario puede editarlo.");
        }

        // Create updated comment with new content
        var newImageUrl = command.newImageUrl() != null ? ImageUrl.of(command.newImageUrl()) : existingComment.imageUrl();
        var updatedComment = new Comment(
                existingComment.id(),
                existingComment.authorId(),
                existingComment.authorProfileId(),
                command.newContent(),
                newImageUrl,
                existingComment.createdAt()
        );

        // Update the comment
        commentRepository.update(updatedComment, command.postId());

        return Optional.of(updatedComment);
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
