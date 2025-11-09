package com.levelup.journey.platform.post.infrastructure.persistence.mongo.repositories;

import com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities.CommentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Mongo repository for comment entities.
 */
@Repository
public interface CommentMongoRepository extends MongoRepository<CommentEntity, String> {

    List<CommentEntity> findByPostIdOrderByCreatedAtDesc(String postId);

    Optional<CommentEntity> findByCommentIdAndPostId(String commentId, String postId);

    void deleteByPostId(String postId);

    void deleteByCommentIdAndPostId(String commentId, String postId);
}
