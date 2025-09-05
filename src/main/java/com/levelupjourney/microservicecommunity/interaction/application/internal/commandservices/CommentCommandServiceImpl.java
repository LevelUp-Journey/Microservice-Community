package com.levelupjourney.microservicecommunity.interaction.application.internal.commandservices;

import com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates.Comment;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.AddCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.DeleteCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.commands.EditCommentCommand;
import com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects.CommentContent;
import com.levelupjourney.microservicecommunity.interaction.domain.services.CommentCommandService;
import com.levelupjourney.microservicecommunity.interaction.infrastructure.persistence.mongodb.repositories.CommentRepository;
import com.levelupjourney.microservicecommunity.posting.infrastructure.persistence.mongodb.repositories.PostRepository;
import com.levelupjourney.microservicecommunity.shared.application.acl.iam.ProfileCacheService;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.CommentId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import com.levelupjourney.microservicecommunity.shared.domain.services.AuthorizationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of comment command service.
 */
@Service
public class CommentCommandServiceImpl implements CommentCommandService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ProfileCacheService profileCacheService;
    private final AuthorizationService authorizationService;
    
    public CommentCommandServiceImpl(CommentRepository commentRepository,
                                   PostRepository postRepository,
                                   ProfileCacheService profileCacheService,
                                   AuthorizationService authorizationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.profileCacheService = profileCacheService;
        this.authorizationService = authorizationService;
    }
    
    @Override
    public Optional<CommentId> handle(AddCommentCommand command) {
        // Validate user exists through ACL
        var userId = UserId.of(command.authorId());
        if (!profileCacheService.validateUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + command.authorId());
        }
        
        // Create and save the comment
        var content = new CommentContent(command.content());
        var comment = Comment.create(
            PostId.of(command.postId()),
            userId,
            content
        );
        
        commentRepository.save(comment);
        return Optional.of(comment.getCommentId());
    }
    
    @Override
    public Optional<CommentId> handle(EditCommentCommand command) {
        var commentId = CommentId.of(command.commentId());
        var userId = UserId.of(command.authorId());
        
        // Validate user exists through ACL
        if (!profileCacheService.validateUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + command.authorId());
        }
        
        // Find the comment
        var comment = commentRepository.findById(commentId.value())
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + command.commentId()));
        
        // Check authorization (only commenter can edit)
        if (!authorizationService.canEditComment(comment.getAuthorId(), userId)) {
            throw new SecurityException("Only the commenter can edit their own comment");
        }
        
        var newContent = new CommentContent(command.newContent());
        comment.edit(newContent);
        
        commentRepository.save(comment);
        return Optional.of(comment.getCommentId());
    }
    
    @Override
    public Optional<CommentId> handle(DeleteCommentCommand command) {
        var commentId = CommentId.of(command.commentId());
        var userId = UserId.of(command.authorId());
        
        // Validate user exists through ACL
        if (!profileCacheService.validateUser(userId)) {
            throw new IllegalArgumentException("User does not exist: " + command.authorId());
        }
        
        // Find the comment
        var comment = commentRepository.findById(commentId.value())
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + command.commentId()));
        
        // Find the post to get the post owner
        var post = postRepository.findByPostId(comment.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + comment.getPostId()));
        
        // Check authorization (comment author, post owner, or admin)
        if (!authorizationService.canDeleteComment(post.getAuthorId(), comment.getAuthorId(), userId)) {
            throw new SecurityException("User is not authorized to delete this comment");
        }
        
        comment.markAsDeleted();
        commentRepository.save(comment);
        
        return Optional.of(comment.getCommentId());
    }
}
