package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

/**
 * Subscription Entity for Cassandra persistence
 * Maps to the subscriptions table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("subscriptions")
public class SubscriptionEntity {

    @PrimaryKey
    @CassandraType(type = CassandraType.Name.UUID)
    private String id;

    @CassandraType(type = CassandraType.Name.UUID)
    private String userId;

    @CassandraType(type = CassandraType.Name.UUID)
    private String communityId;

    private Instant createdAt;
}
