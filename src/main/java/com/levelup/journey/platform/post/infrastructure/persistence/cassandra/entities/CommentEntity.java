package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.Instant;

/**
 * Comment Entity for Cassandra
 * User Defined Type for nested comments in posts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@UserDefinedType("comment")
public class CommentEntity {
    @CassandraType(type = CassandraType.Name.UUID)
    private String id;

    @CassandraType(type = CassandraType.Name.UUID)
    private String authorId;

    private String content;
    private Instant createdAt;
}
