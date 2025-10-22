package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.adapters;

import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.repositories.CommentRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.CommentEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories.CommentCassandraRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Comment Repository Adapter
 * Adapts domain Comment repository to Cassandra persistence
 */
@Component
public class CommentRepositoryAdapter implements CommentRepository {

    private final CommentCassandraRepository cassandraRepository;

    public CommentRepositoryAdapter(CommentCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @Override
    public Comment save(Comment comment, PostId postId) {
        CommentEntity entity = toEntity(comment, postId);
        cassandraRepository.save(entity);
        return comment;
    }

    @Override
    public List<Comment> findByPostId(PostId postId) {
        return cassandraRepository.findByPostId(postId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Comment> findByIdAndPostId(CommentId commentId, PostId postId) {
        return cassandraRepository.findByPostId(postId.value()).stream()
                .filter(entity -> entity.getCommentId().equals(commentId.value()))
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public void deleteByPostId(PostId postId) {
        cassandraRepository.deleteByPostId(postId.value());
    }

    /**
     * Convert domain Comment to Cassandra entity
     */
    private CommentEntity toEntity(Comment comment, PostId postId) {
        return new CommentEntity(
                postId.value(),
                comment.id().value(),
                comment.authorId().value(),
                comment.content(),
                comment.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Comment
     */
    private Comment toDomain(CommentEntity entity) {
        return new Comment(
                CommentId.of(entity.getCommentId()),
                UserId.of(entity.getAuthorId()),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
