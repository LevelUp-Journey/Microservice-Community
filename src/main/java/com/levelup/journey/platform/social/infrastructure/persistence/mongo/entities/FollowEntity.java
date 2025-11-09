package com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Follow relationship stored in MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follows")
public class FollowEntity {

    @Id
    private String id;

    @Indexed(name = "idx_follows_follower")
    private String followerId;

    @Indexed(name = "idx_follows_following")
    private String followingId;

    private Instant createdAt;
}
