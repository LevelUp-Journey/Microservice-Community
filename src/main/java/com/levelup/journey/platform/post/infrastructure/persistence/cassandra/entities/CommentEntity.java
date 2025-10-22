package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String id;
    private String authorId;
    private String content;
    private Instant createdAt;
}
