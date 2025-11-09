package com.levelup.journey.platform.post.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.repositories.CommentRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities.CommentEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.repositories.CommentMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Comment repository adapter backed by MongoDB.
 */
@Component
public class CommentRepositoryAdapter implements CommentRepository {

    private final CommentMongoRepository mongoRepository;

    public CommentRepositoryAdapter(CommentMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Comment save(Comment comment, PostId postId) {
        CommentEntity entity = toEntity(comment, postId);
        mongoRepository.save(entity);
        return comment;
    }

    @Override
    public List<Comment> findByPostId(PostId postId) {
        return mongoRepository.findByPostIdOrderByCreatedAtDesc(postId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Comment> findByIdAndPostId(CommentId commentId, PostId postId) {
        return mongoRepository.findByCommentIdAndPostId(commentId.value(), postId.value())
                .map(this::toDomain);
    }

    @Override
    public boolean deleteByIdAndPostId(CommentId commentId, PostId postId) {
        Optional<CommentEntity> entityOptional = mongoRepository.findByCommentIdAndPostId(commentId.value(), postId.value());

        if (entityOptional.isPresent()) {
            mongoRepository.delete(entityOptional.get());
            return true;
        }
        return false;
    }

    @Override
    public Comment update(Comment comment, PostId postId) {
        CommentEntity entity = toEntity(comment, postId);
        mongoRepository.save(entity);
        return comment;
    }

    @Override
    public void deleteByPostId(PostId postId) {
        mongoRepository.deleteByPostId(postId.value());
    }

    private CommentEntity toEntity(Comment comment, PostId postId) {
        return new CommentEntity(
                comment.id().value(),
                postId.value(),
                comment.authorId().value(),
                comment.authorProfileId().value(),
                comment.content(),
                comment.imageUrl() != null && !comment.imageUrl().isEmpty() ? comment.imageUrl().url() : null,
                comment.createdAt()
        );
    }

    private Comment toDomain(CommentEntity entity) {
        ImageUrl imageUrl = entity.getImageUrl() != null ? ImageUrl.of(entity.getImageUrl()) : ImageUrl.empty();
        return new Comment(
                CommentId.of(entity.getCommentId()),
                UserId.of(entity.getAuthorId()),
                ProfileId.of(entity.getAuthorProfileId()),
                entity.getContent(),
                imageUrl,
                entity.getCreatedAt()
        );
    }
}
