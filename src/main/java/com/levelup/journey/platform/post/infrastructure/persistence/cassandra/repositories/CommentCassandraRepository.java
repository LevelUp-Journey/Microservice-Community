package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.CommentEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Cassandra Repository for Comment entities
 * Comments are stored in a separate table with postId as partition key
 * Using derived query methods for automatic query generation
 */
@Repository
public interface CommentCassandraRepository extends CassandraRepository<CommentEntity, String> {

    /**
     * Find all comments for a specific post
     * Spring Data will automatically generate the query based on method name
     * @param postId the post identifier (as String UUID)
     * @return list of comments ordered by creation time (newest first)
     */
    List<CommentEntity> findByPostId(String postId);

    /**
     * Delete all comments for a specific post
     * Spring Data will automatically generate the delete query
     * @param postId the post identifier (as String UUID)
     */
    void deleteByPostId(String postId);
}
