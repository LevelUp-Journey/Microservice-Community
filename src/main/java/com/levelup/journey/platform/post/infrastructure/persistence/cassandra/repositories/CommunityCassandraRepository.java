package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories;

import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.CommunityEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Cassandra Repository for Community entities
 */
@Repository
public interface CommunityCassandraRepository extends CassandraRepository<CommunityEntity, String> {
}
