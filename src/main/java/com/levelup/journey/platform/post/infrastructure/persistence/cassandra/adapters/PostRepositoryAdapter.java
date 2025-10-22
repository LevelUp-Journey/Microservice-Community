package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.adapters;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommentId;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.CommentEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.PostEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories.PostCassandraRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Post Repository Adapter
 * Adapts domain Post repository to Cassandra persistence
 */
@Component
public class PostRepositoryAdapter implements PostRepository {

    private final PostCassandraRepository cassandraRepository;

    public PostRepositoryAdapter(PostCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @Override
    public Post save(Post post) {
        PostEntity entity = toEntity(post);
        cassandraRepository.save(entity);
        return post;
    }

    @Override
    public Optional<Post> findById(PostId id) {
        return cassandraRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Post> findAll() {
        return cassandraRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Find posts by community ID
     * @param communityId the community identifier
     * @return list of posts
     */
    public List<Post> findByCommunityId(CommunityId communityId) {
        return cassandraRepository.findByCommunityId(communityId.value()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert domain Post to Cassandra entity
     */
    private PostEntity toEntity(Post post) {
        PostEntity entity = new PostEntity();
        entity.setId(post.id().value());
        entity.setCommunityId(post.communityId().value());
        entity.setAuthorId(post.authorId().value());
        entity.setTitle(post.title());
        entity.setContent(post.content());
        entity.setCreatedAt(post.createdAt());

        List<CommentEntity> commentEntities = post.comments().stream()
                .map(this::toCommentEntity)
                .collect(Collectors.toList());
        entity.setComments(commentEntities);

        return entity;
    }

    /**
     * Convert Cassandra entity to domain Post
     */
    private Post toDomain(PostEntity entity) {
        List<Comment> comments = entity.getComments().stream()
                .map(this::toCommentDomain)
                .collect(Collectors.toList());

        return Post.restore(
                PostId.of(entity.getId()),
                CommunityId.of(entity.getCommunityId()),
                UserId.of(entity.getAuthorId()),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                comments
        );
    }

    /**
     * Convert domain Comment to Cassandra entity
     */
    private CommentEntity toCommentEntity(Comment comment) {
        return new CommentEntity(
                comment.id().value(),
                comment.authorId().value(),
                comment.content(),
                comment.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Comment
     */
    private Comment toCommentDomain(CommentEntity entity) {
        return new Comment(
                CommentId.of(entity.getId()),
                UserId.of(entity.getAuthorId()),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
