package com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates;

import com.levelupjourney.microservicecommunity.interaction.domain.model.events.CommentAdded;
import com.levelupjourney.microservicecommunity.interaction.domain.model.events.CommentDeleted;
import com.levelupjourney.microservicecommunity.interaction.domain.model.events.CommentEdited;
import com.levelupjourney.microservicecommunity.interaction.domain.model.valueobjects.CommentContent;
import com.levelupjourney.microservicecommunity.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.CommentId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Comment aggregate root representing a comment on a post.
 * Manages the lifecycle of comments including creation, editing, and deletion.
 */
@Getter
@Document(collection = "comments")
public class Comment extends AuditableAbstractAggregateRoot<Comment> {
    
    @Field("comment_id")
    private CommentId commentId;
    
    @Field("post_id")
    private PostId postId;
    
    @Field("author_id")
    private UserId authorId;
    
    @Field("content")
    private CommentContent content;
    
    @Field("edited_at")
    private LocalDateTime editedAt;
    
    @Field("deleted")
    private boolean deleted;
    
    // Required by Spring Data MongoDB
    protected Comment() {}
    
    private Comment(CommentId commentId, PostId postId, UserId authorId, CommentContent content) {
        this.setId(commentId.value());  // Set the inherited MongoDB ID
        this.commentId = commentId;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.deleted = false;
        this.editedAt = null;
    }
    
    /**
     * Creates a new comment on a post.
     * 
     * @param postId the ID of the post being commented on
     * @param authorId the author of the comment
     * @param content the comment content
     * @return a new Comment instance
     */
    public static Comment create(PostId postId, UserId authorId, CommentContent content) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("Comment content cannot be null");
        }
        
        var commentId = CommentId.generate();
        var comment = new Comment(commentId, postId, authorId, content);
        
        // Register domain event
        comment.addDomainEvent(new CommentAdded(
            commentId.value(),
            postId.value(),
            authorId.value(),
            content,
            LocalDateTime.now()
        ));
        
        return comment;
    }
    
    /**
     * Edits the comment content.
     * 
     * @param newContent the new comment content
     * @param editorId the ID of the user editing the comment
     */
    public void edit(CommentContent newContent, UserId editorId) {
        if (newContent == null) {
            throw new IllegalArgumentException("New content cannot be null");
        }
        if (editorId == null) {
            throw new IllegalArgumentException("Editor ID cannot be null");
        }
        if (this.deleted) {
            throw new IllegalStateException("Cannot edit a deleted comment");
        }
        if (!this.authorId.equals(editorId)) {
            throw new SecurityException("Only the commenter can edit their own comment");
        }
        
        this.content = newContent;
        this.editedAt = LocalDateTime.now();
        
        // Register domain event
        this.addDomainEvent(new CommentEdited(
            this.commentId.value(),
            newContent,
            this.editedAt
        ));
    }
    
    /**
     * Edits the comment content (simplified version for use after authorization check).
     * 
     * @param newContent the new comment content
     */
    public void edit(CommentContent newContent) {
        if (newContent == null) {
            throw new IllegalArgumentException("New content cannot be null");
        }
        if (this.deleted) {
            throw new IllegalStateException("Cannot edit a deleted comment");
        }
        
        this.content = newContent;
        this.editedAt = LocalDateTime.now();
        
        // Register domain event
        this.addDomainEvent(new CommentEdited(
            this.commentId.value(),
            newContent,
            this.editedAt
        ));
    }
    
    /**
     * Deletes the comment (soft delete).
     * Authorization check must be done at service level.
     * 
     * @param deleterId the ID of the user deleting the comment
     */
    public void delete(UserId deleterId) {
        if (deleterId == null) {
            throw new IllegalArgumentException("Deleter ID cannot be null");
        }
        if (this.deleted) {
            throw new IllegalStateException("Comment is already deleted");
        }
        // Note: Authorization check (comment author, post owner, or admin) should be done at service level
        
        this.deleted = true;
        var deletedAt = LocalDateTime.now();
        
        // Register domain event
        this.addDomainEvent(new CommentDeleted(
            this.commentId.value(),
            deletedAt
        ));
    }
    
    /**
     * Checks if the comment has been edited.
     */
    public boolean isEdited() {
        return editedAt != null;
    }
    
    /**
     * Checks if the comment is active (not deleted).
     */
    public boolean isActive() {
        return !deleted;
    }
    
    /**
     * Marks this comment as deleted.
     */
    public void markAsDeleted() {
        if (this.deleted) {
            throw new IllegalStateException("Comment is already deleted");
        }
        
        this.deleted = true;
        var deletedAt = LocalDateTime.now();
        
        this.addDomainEvent(new CommentDeleted(
            this.commentId.value(),
            deletedAt
        ));
    }
}
