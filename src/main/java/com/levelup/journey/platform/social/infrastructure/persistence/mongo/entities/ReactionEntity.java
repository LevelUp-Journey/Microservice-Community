package com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Reaction document persisted in MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reactions")
public class ReactionEntity {

    /**
     * Synthetic identifier composed by `${postId}-${userId}` to enforce uniqueness.
     */
    @Id
    private String id;

    @Indexed(name = "idx_reactions_post")
    private String postId;

    @Indexed(name = "idx_reactions_user")
    private String userId;

    private String reactionType;
    private Instant createdAt;
}
