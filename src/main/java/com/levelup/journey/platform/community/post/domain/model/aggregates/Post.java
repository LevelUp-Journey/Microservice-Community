package com.levelup.journey.platform.community.post.domain.model.aggregates;

import com.levelup.journey.platform.community.post.domain.model.valueobjects.Body;
import com.levelup.journey.platform.community.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.community.post.domain.model.valueobjects.Title;
import com.levelup.journey.platform.community.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelup.journey.platform.community.shared.domain.model.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Post Aggregate Root.
 * Represents a post in the community platform.
 * Only users with DOCENTE role can create, update, or delete posts.
 */
@Getter
@Entity
@Table(name = "posts")
public class Post extends AuditableAbstractAggregateRoot<Post> {

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false, length = 255))
    private Title title;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "body", nullable = false, columnDefinition = "TEXT"))
    private Body body;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "image_url"))
    private ImageUrl imageUrl;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * Default constructor for JPA.
     */
    protected Post() {
    }

    /**
     * Constructor to create a new post.
     *
     * @param userId the ID of the user creating the post
     * @param title the title of the post
     * @param body the body content of the post
     * @param imageUrl optional image URL
     */
    public Post(UserId userId, Title title, Body body, ImageUrl imageUrl) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.isDeleted = false;
    }

    /**
     * Updates the post content.
     * Only the author can update their post.
     *
     * @param title the new title
     * @param body the new body content
     * @param imageUrl the new image URL
     */
    public void update(Title title, Body body, ImageUrl imageUrl) {
        if (this.isDeleted) {
            throw new IllegalStateException("Cannot update a deleted post");
        }
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
    }

    /**
     * Performs logical deletion of the post.
     * Only the author can delete their post.
     */
    public void delete() {
        if (this.isDeleted) {
            throw new IllegalStateException("Post is already deleted");
        }
        this.isDeleted = true;
    }

    /**
     * Checks if the given user is the author of this post.
     *
     * @param userId the user ID to check
     * @return true if the user is the author, false otherwise
     */
    public boolean isAuthor(UserId userId) {
        return this.userId.equals(userId);
    }
}
