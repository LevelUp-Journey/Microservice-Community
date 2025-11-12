package com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Post entity stored in MongoDB.
 * Comments are persisted in a separate collection.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class PostEntity {

    @Id
    private String id;

    @Indexed(name = "idx_posts_community_id")
    private String communityId;

    private String authorId;
    private String authorProfileId;
    private String content;
    private String imageUrl;
    private Instant createdAt;
}
