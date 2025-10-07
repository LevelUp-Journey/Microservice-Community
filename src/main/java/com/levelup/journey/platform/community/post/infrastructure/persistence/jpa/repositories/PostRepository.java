package com.levelup.journey.platform.community.post.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.community.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.community.shared.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Post aggregate.
 * Provides data access methods for posts.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Find a post by ID that is not deleted.
     *
     * @param id the post ID
     * @return Optional containing the post if found and not deleted
     */
    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    /**
     * Find all posts that are not deleted, ordered by creation date descending.
     *
     * @return List of non-deleted posts
     */
    List<Post> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    /**
     * Find all posts created by a specific user that are not deleted.
     *
     * @param userId the user ID
     * @return List of posts by the user
     */
    List<Post> findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(UserId userId);
}
