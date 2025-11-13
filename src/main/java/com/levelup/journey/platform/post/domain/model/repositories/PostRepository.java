package com.levelup.journey.platform.post.domain.model.repositories;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.valueobjects.CommunityId;
import com.levelup.journey.platform.post.domain.model.valueobjects.PostId;

import java.util.List;
import java.util.Optional;

/** Puerto de repositorio para Post. */
public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(PostId id);
    List<Post> findAll();
    List<Post> findAll(int page, int size);
    long count();
    List<Post> findByCommunityId(CommunityId communityId, int page, int size);
    long countByCommunityId(CommunityId communityId);
    void deleteById(PostId id);

    /**
     * Find posts by source IDs (author IDs or community IDs) with pagination
     * This method is used for feed generation
     * @param sourceIds List of source IDs (can be author IDs or community IDs)
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of posts from the specified sources, ordered by creation date (newest first)
     */
    List<Post> findBySourceIds(List<String> sourceIds, int page, int size);
}

