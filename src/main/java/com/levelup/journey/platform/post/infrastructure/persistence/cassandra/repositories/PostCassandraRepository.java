package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.PostEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Cassandra Repository for Post entities
 */
@Repository
public interface PostCassandraRepository extends CassandraRepository<PostEntity, String> {

    /**
     * Find all posts by community ID
     * @param communityId the community identifier
     * @return list of posts
     */
    List<PostEntity> findByCommunityId(String communityId);
}
