package com.levelupjourney.microservicecommunity.interaction.infrastructure.persistence.mongodb.repositories;

import com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates.Like;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for Like aggregates.
 * Handles persistence operations for post likes.
 */
@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    
    /**
     * Finds a like by post ID and user ID (composite key).
     */
    Optional<Like> findByPostIdAndUserId(PostId postId, UserId userId);
    
    /**
     * Finds a like by post ID and user ID using string values.
     */
    @Query("{ 'post_id.value': ?0, 'user_id.value': ?1 }")
    Optional<Like> findByPostIdAndUserId(String postId, String userId);
    
    /**
     * Finds all likes for a specific post with pagination.
     */
    Page<Like> findByPostIdOrderByLikedAtDesc(PostId postId, Pageable pageable);
    
    /**
     * Finds all likes by a specific user with pagination.
     */
    Page<Like> findByUserIdOrderByLikedAtDesc(UserId userId, Pageable pageable);
    
    /**
     * Counts likes for a specific post.
     */
    long countByPostId(PostId postId);
    
    /**
     * Counts likes by a specific user.
     */
    long countByUserId(UserId userId);
    
    /**
     * Checks if a user has liked a specific post.
     */
    boolean existsByPostIdAndUserId(PostId postId, UserId userId);
    
    /**
     * Deletes a like by post ID and user ID.
     */
    void deleteByPostIdAndUserId(PostId postId, UserId userId);
    
    /**
     * Gets user IDs who liked a specific post.
     */
    @Query(value = "{ 'postId': ?0 }", fields = "{ 'userId': 1 }")
    Page<UserId> findUserIdsByPostId(PostId postId, Pageable pageable);
}
