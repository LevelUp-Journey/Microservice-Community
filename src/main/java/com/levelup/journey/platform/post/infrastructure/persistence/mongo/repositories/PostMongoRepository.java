package com.levelup.journey.platform.post.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Mongo repository for post entities.
 */
@Repository
public interface PostMongoRepository extends MongoRepository<PostEntity, String> {

    List<PostEntity> findByCommunityIdOrderByCreatedAtDesc(String communityId, Pageable pageable);
    List<PostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByCommunityId(String communityId);
}
