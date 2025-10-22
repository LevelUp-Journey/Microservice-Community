package com.levelup.journey.platform.post.domain.model.repositories;

import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Comment entity
 * Provides persistence operations for comments
 */
public interface CommentRepository {

    /**
     * Save a comment
     * @param comment the comment to save
     * @param postId the post ID this comment belongs to
     * @return the saved comment
     */
    Comment save(Comment comment, PostId postId);

    /**
     * Find all comments for a specific post
     * @param postId the post identifier
     * @return list of comments ordered by creation time (newest first)
     */
    List<Comment> findByPostId(PostId postId);

    /**
     * Find a comment by its ID and post ID
     * @param commentId the comment identifier
     * @param postId the post identifier
     * @return an optional containing the comment if found
     */
    Optional<Comment> findByIdAndPostId(CommentId commentId, PostId postId);

    /**
     * Delete all comments for a specific post
     * @param postId the post identifier
     */
    void deleteByPostId(PostId postId);
}
