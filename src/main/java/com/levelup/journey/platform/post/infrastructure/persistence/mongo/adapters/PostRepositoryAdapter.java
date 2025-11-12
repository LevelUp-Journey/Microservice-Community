package com.levelup.journey.platform.post.infrastructure.persistence.mongo.adapters;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ProfileId;
import com.levelup.journey.platform.post.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.post.domain.model.valueobjects.ImageUrl;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.entities.PostEntity;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.repositories.PostMongoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mongo-backed repository adapter for posts.
 * Comments are managed separately through CommentRepository.
 */
@Component
public class PostRepositoryAdapter implements PostRepository {

    private final PostMongoRepository mongoRepository;

    public PostRepositoryAdapter(PostMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Post save(Post post) {
        PostEntity entity = toEntity(post);
        mongoRepository.save(entity);
        return post;
    }

    @Override
    public Optional<Post> findById(PostId id) {
        return mongoRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Post> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mongoRepository.findAllByOrderByCreatedAtDesc(pageable).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByCommunityId(CommunityId communityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mongoRepository.findByCommunityIdOrderByCreatedAtDesc(communityId.value(), pageable).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return mongoRepository.count();
    }

    @Override
    public long countByCommunityId(CommunityId communityId) {
        return mongoRepository.countByCommunityId(communityId.value());
    }

    @Override
    public void deleteById(PostId id) {
        mongoRepository.deleteById(id.value());
    }

    /**
     * Find posts by community ID (deprecated - use paginated version)
     * @param communityId the community identifier
     * @return list of posts
     */
    public List<Post> findByCommunityId(CommunityId communityId) {
        Pageable pageable = PageRequest.of(0, 100); // Default to first 100
        return mongoRepository.findByCommunityIdOrderByCreatedAtDesc(communityId.value(), pageable).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private PostEntity toEntity(Post post) {
        return new PostEntity(
                post.id().value(),
                post.communityId().value(),
                post.authorId().value(),
                post.authorProfileId().value(),
                post.content(),
                post.imageUrl() != null && !post.imageUrl().isEmpty() ? post.imageUrl().url() : null,
                post.createdAt()
        );
    }

    private Post toDomain(PostEntity entity) {
        ImageUrl imageUrl = entity.getImageUrl() != null ? ImageUrl.of(entity.getImageUrl()) : ImageUrl.empty();
        return Post.restore(
                PostId.of(entity.getId()),
                CommunityId.of(entity.getCommunityId()),
                UserId.of(entity.getAuthorId()),
                ProfileId.of(entity.getAuthorProfileId()),
                entity.getContent(),
                imageUrl,
                entity.getCreatedAt()
        );
    }
}
