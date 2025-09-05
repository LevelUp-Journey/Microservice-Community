package com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates;

import com.levelupjourney.microservicecommunity.interaction.domain.model.events.PostLiked;
import com.levelupjourney.microservicecommunity.interaction.domain.model.events.PostUnliked;
import com.levelupjourney.microservicecommunity.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Like aggregate root representing a user's like on a post.
 * Uses composite key (postId, userId) to enforce uniqueness.
 */
@Getter
@Document(collection = "likes")
public class Like extends AuditableAbstractAggregateRoot<Like> {
    
    @Field("post_id")
    private PostId postId;
    
    @Field("user_id")
    private UserId userId;
    
    @Field("liked_at")
    private LocalDateTime likedAt;
    
    // Required by Spring Data MongoDB
    protected Like() {}
    
    private Like(PostId postId, UserId userId) {
        this.postId = postId;
        this.userId = userId;
        this.likedAt = LocalDateTime.now();
    }
    
    /**
     * Creates a new like for a post by a user.
     * 
     * @param postId the ID of the post being liked
     * @param userId the ID of the user liking the post
     * @return a new Like instance
     */
    public static Like create(PostId postId, UserId userId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        var like = new Like(postId, userId);
        
        // Register domain event
        like.addDomainEvent(new PostLiked(
            postId.value(),
            userId.value(),
            like.likedAt
        ));
        
        return like;
    }
    
    /**
     * Removes the like (for unlike operation).
     * This method should be called before deleting the entity.
     */
    public void unlike() {
        var deletedAt = LocalDateTime.now();
        
        // Register domain event
        this.addDomainEvent(new PostUnliked(
            this.postId.value(),
            this.userId.value(),
            deletedAt
        ));
    }
    
    /**
     * Gets the composite key for this like.
     */
    public String getCompositeKey() {
        return postId.value() + ":" + userId.value();
    }
}
