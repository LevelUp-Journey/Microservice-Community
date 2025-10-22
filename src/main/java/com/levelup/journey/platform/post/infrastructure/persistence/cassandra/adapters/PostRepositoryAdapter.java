package com.levelup.journey.platform.post.infrastructure.persistence.cassandra.adapters;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.entities.PostEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.repositories.PostCassandraRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Post Repository Adapter
 * Adapts domain Post repository to Cassandra persistence
 * Comments are managed separately through CommentRepository
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
     * Note: Comments are not stored here - they are in a separate table
     */
    private PostEntity toEntity(Post post) {
        return new PostEntity(
                post.id().value(),
                post.communityId().value(),
                post.authorId().value(),
                post.authorProfileId().value(),
                post.title(),
                post.content(),
                post.imageUrl() != null && !post.imageUrl().isEmpty() ? post.imageUrl().url() : null,
                post.createdAt()
        );
    }

    /**
     * Convert Cassandra entity to domain Post
     * Note: Comments are loaded separately via CommentRepository
     */
    private Post toDomain(PostEntity entity) {
        ImageUrl imageUrl = entity.getImageUrl() != null ? ImageUrl.of(entity.getImageUrl()) : ImageUrl.empty();
        return Post.restore(
                PostId.of(entity.getId()),
                CommunityId.of(entity.getCommunityId()),
                UserId.of(entity.getAuthorId()),
                ProfileId.of(entity.getAuthorProfileId()),
                entity.getTitle(),
                entity.getContent(),
                imageUrl,
                entity.getCreatedAt(),
                List.of() // Comments loaded separately when needed
        );
    }
}
