package com.levelup.journey.platform.social.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

/**
 * Follow Primary Key for Cassandra
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyClass
public class FollowPrimaryKey implements Serializable {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID followerId;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID followingId;
}