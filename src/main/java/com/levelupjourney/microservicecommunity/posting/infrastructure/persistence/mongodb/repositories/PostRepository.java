package com.levelupjourney.microservicecommunity.posting.infrastructure.persistence.mongodb.repositories;

import com.levelupjourney.microservicecommunity.posting.domain.model.aggregates.Post;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for Post aggregates.
 * Handles persistence operations for posts in the write side.
 */
@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    /**
     * Finds a post by its PostId.
     */
    Optional<Post> findByPostId(PostId postId);
    
    /**
     * Finds posts by author ID with pagination.
     */
    Page<Post> findByAuthorIdAndDeletedFalse(UserId authorId, Pageable pageable);
    
    /**
     * Finds all active (non-deleted) posts.
     */
    Page<Post> findByDeletedFalse(Pageable pageable);
    
    /**
     * Checks if a post exists by PostId and is not deleted.
     */
    @Query("{ 'postId': ?0, 'deleted': false }")
    boolean existsByPostIdAndNotDeleted(PostId postId);
    
    /**
     * Counts posts by author ID (excluding deleted).
     */
    long countByAuthorIdAndDeletedFalse(UserId authorId);
}
