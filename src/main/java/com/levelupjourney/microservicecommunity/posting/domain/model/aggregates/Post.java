package com.levelupjourney.microservicecommunity.posting.domain.model.aggregates;

import com.levelupjourney.microservicecommunity.posting.domain.model.events.PostCreated;
import com.levelupjourney.microservicecommunity.posting.domain.model.events.PostDeleted;
import com.levelupjourney.microservicecommunity.posting.domain.model.events.PostEdited;
import com.levelupjourney.microservicecommunity.posting.domain.model.valueobjects.PostBody;
import com.levelupjourney.microservicecommunity.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Post aggregate root representing a social media post.
 * Manages the lifecycle of posts including creation, editing, and deletion.
 */
@Getter
@Document(collection = "posts")
public class Post extends AuditableAbstractAggregateRoot<Post> {
    
    @Field("post_id")
    private PostId postId;
    
    @Field("author_id")
    private UserId authorId;
    
    @Field("content")
    private PostBody content;
    
    @Field("edited_at")
    private LocalDateTime editedAt;
    
    @Field("deleted")
    private boolean deleted;
    
    // Required by Spring Data MongoDB
    protected Post() {}
    
    private Post(PostId postId, UserId authorId, PostBody content) {
        this.setId(postId.value());  // Set the inherited MongoDB ID
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.deleted = false;
        this.editedAt = null;
    }
    
    /**
     * Creates a new post with the given content.
     * 
     * @param authorId the author of the post
     * @param content the post content
     * @return a new Post instance
     */
    public static Post create(UserId authorId, PostBody content) {
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("Post content cannot be null");
        }
        
        var postId = PostId.generate();
        var post = new Post(postId, authorId, content);
        
        // Register domain event
        post.addDomainEvent(new PostCreated(
            postId.value(),
            authorId.value(),
            content,
            LocalDateTime.now()
        ));
        
        return post;
    }
    
    /**
     * Edits the post content.
     * 
     * @param newContent the new post content
     * @param editorId the ID of the user editing the post
     */
    public void edit(PostBody newContent, UserId editorId) {
        if (newContent == null) {
            throw new IllegalArgumentException("New content cannot be null");
        }
        if (editorId == null) {
            throw new IllegalArgumentException("Editor ID cannot be null");
        }
        if (this.deleted) {
            throw new IllegalStateException("Cannot edit a deleted post");
        }
        if (!this.authorId.equals(editorId)) {
            throw new SecurityException("Only the post owner can edit the post");
        }
        
        this.content = newContent;
        this.editedAt = LocalDateTime.now();
        
        // Register domain event
        this.addDomainEvent(new PostEdited(
            this.postId.value(),
            newContent,
            this.editedAt
        ));
    }
    
    /**
     * Deletes the post (soft delete).
     * Only the post owner or admin can delete.
     * Authorization check must be done at service level.
     * 
     * @param deleterId the ID of the user deleting the post
     */
    public void delete(UserId deleterId) {
        if (deleterId == null) {
            throw new IllegalArgumentException("Deleter ID cannot be null");
        }
        if (this.deleted) {
            throw new IllegalStateException("Post is already deleted");
        }
        // Note: Authorization check (post owner or admin) should be done at service level
        
        this.deleted = true;
        var deletedAt = LocalDateTime.now();
        
        // Register domain event
        this.addDomainEvent(new PostDeleted(
            this.postId.value(),
            deletedAt
        ));
    }
    
    /**
     * Checks if the post has been edited.
     */
    public boolean isEdited() {
        return editedAt != null;
    }
    
    /**
     * Checks if the post is active (not deleted).
     */
    public boolean isActive() {
        return !deleted;
    }
}
