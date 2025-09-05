package com.levelupjourney.microservicecommunity.interaction.infrastructure.persistence.mongodb.repositories;

import com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates.Comment;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.CommentId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for Comment aggregates.
 * Handles persistence operations for comments.
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    /**
     * Finds a comment by its CommentId.
     */
    Optional<Comment> findByCommentId(CommentId commentId);
    
    /**
     * Finds comments by post ID with pagination (excluding deleted).
     */
    Page<Comment> findByPostIdAndDeletedFalseOrderByCreatedAtAsc(PostId postId, Pageable pageable);
    
    /**
     * Finds comments by post ID (excluding deleted) - used for listing all comments.
     */
    @Query(value = "{ 'post_id.value': ?0, 'deleted': false }", sort = "{ 'createdAt': 1 }")
    List<Comment> findByPostIdAndDeletedFalseOrderByCreatedAtAsc(String postId);
    
    /**
     * Finds comments by author ID with pagination (excluding deleted).
     */
    Page<Comment> findByAuthorIdAndDeletedFalse(UserId authorId, Pageable pageable);
    
    /**
     * Counts comments for a specific post (excluding deleted).
     */
    long countByPostIdAndDeletedFalse(PostId postId);
    
    /**
     * Counts comments by author ID (excluding deleted).
     */
    long countByAuthorIdAndDeletedFalse(UserId authorId);
    
    /**
     * Checks if a comment exists by CommentId and is not deleted.
     */
    @Query("{ 'commentId': ?0, 'deleted': false }")
    boolean existsByCommentIdAndNotDeleted(CommentId commentId);
}
