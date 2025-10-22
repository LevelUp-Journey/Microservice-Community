package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

/**
 * Comment Entity for Cassandra
 * Separate table for comments with composite primary key (postId, commentId)
 * This allows efficient querying of all comments for a post
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("comments")
public class CommentEntity {

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.UUID)
    private String postId;

    @PrimaryKeyColumn(name = "comment_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @CassandraType(type = CassandraType.Name.UUID)
    private String commentId;

    @CassandraType(type = CassandraType.Name.UUID)
    private String authorId;

    private String content;
    private String imageUrl;
    private Instant createdAt;
}
