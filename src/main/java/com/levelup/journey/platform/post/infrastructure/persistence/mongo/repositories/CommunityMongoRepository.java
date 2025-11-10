package com.levelup.journey.platform.post.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities.CommunityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Mongo repository for community entities.
 */
@Repository
public interface CommunityMongoRepository extends MongoRepository<CommunityEntity, String> {
}
