package com.levelup.journey.platform.social.infrastructure.persistence.mongo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Subscription document for MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscriptions")
public class SubscriptionEntity {

    @Id
    private String id;

    @Indexed(name = "idx_subscriptions_user")
    private String userId;

    @Indexed(name = "idx_subscriptions_community")
    private String communityId;

    private Instant createdAt;
}
