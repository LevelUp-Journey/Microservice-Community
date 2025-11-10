package com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Comment entity stored in MongoDB.
 * Comments are stored in their own collection and reference posts by ID.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class CommentEntity {

    @Id
    private String commentId;

    @Indexed(name = "idx_comments_post_id")
    private String postId;

    private String authorId;
    private String authorProfileId;
    private String content;
    private String imageUrl;
    private Instant createdAt;
}
