package com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Community entity stored in MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communities")
public class CommunityEntity {

    @Id
    private String id;

    private String ownerId;
    private String ownerProfileId;
    private String name;
    private String description;
    private String imageUrl;
    private Instant createdAt;
}
