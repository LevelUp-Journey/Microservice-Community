package com.levelup.journey.platform.post.application.internal.queryservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.adapters.PostRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Post Query Service Implementation
 * Handles queries for Post aggregate
 */
@Service
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;
    private final PostRepositoryAdapter postRepositoryAdapter;

    public PostQueryServiceImpl(PostRepository postRepository,
                                PostRepositoryAdapter postRepositoryAdapter) {
        this.postRepository = postRepository;
        this.postRepositoryAdapter = postRepositoryAdapter;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> handle(GetPostByIdQuery query) {
        return postRepository.findById(query.id());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetAllPostsQuery query) {
        List<Post> posts = postRepository.findAll();

        // Sort by creation date descending (most recent first)
        posts.sort((p1, p2) -> p2.createdAt().compareTo(p1.createdAt()));

        return posts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetPostsByCommunityIdQuery query) {
        List<Post> posts = postRepositoryAdapter.findByCommunityId(query.communityId());

        // Sort by creation date descending (most recent first)
        posts.sort((p1, p2) -> p2.createdAt().compareTo(p1.createdAt()));

        return posts;
    }
}
