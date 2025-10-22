package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Post Entity for Cassandra persistence
 * Maps to the posts table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("posts")
public class PostEntity {

    @PrimaryKey
    @CassandraType(type = CassandraType.Name.UUID)
    private String id;

    @CassandraType(type = CassandraType.Name.UUID)
    private String communityId;

    @CassandraType(type = CassandraType.Name.UUID)
    private String authorId;

    private String title;
    private String content;
    private Instant createdAt;
    private List<CommentEntity> comments = new ArrayList<>();
}
