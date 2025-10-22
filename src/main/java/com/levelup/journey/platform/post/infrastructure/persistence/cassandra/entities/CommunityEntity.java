package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

/**
 * Community Entity for Cassandra persistence
 * Maps to the communities table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("communities")
public class CommunityEntity {

    @PrimaryKey
    private String id;

    private String ownerId;
    private String name;
    private String description;
    private Instant createdAt;
}
