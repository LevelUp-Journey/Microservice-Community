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

    /**
     * Find posts where either the author ID or community ID is in the provided list
     * This is used for feed generation to fetch posts from followed users and subscribed communities
     * @param authorIds List of author IDs
     * @param communityIds List of community IDs
     * @param pageable Pagination information
     * @return List of posts ordered by creation date descending
     */
    List<PostEntity> findByAuthorIdInOrCommunityIdInOrderByCreatedAtDesc(List<String> authorIds, List<String> communityIds, Pageable pageable);
}
