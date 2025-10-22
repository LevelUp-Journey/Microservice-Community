package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

/**
 * Follow Entity for Cassandra persistence
 * Maps to the follows table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("follows")
public class FollowEntity {

    @PrimaryKey
    @CassandraType(type = CassandraType.Name.UUID)
    private String id;

    @CassandraType(type = CassandraType.Name.UUID)
    private String followerId;

    @CassandraType(type = CassandraType.Name.UUID)
    private String followingId;

    private Instant createdAt;
}
