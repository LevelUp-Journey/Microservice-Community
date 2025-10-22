package com.levelup.journey.platform.post.application.internal.queryservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
import com.levelup.journey.platform.post.infrastructure.persistence.cassandra.adapters.PostRepositoryAdapter;
import org.springframework.stereotype.Service;

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
    public Optional<Post> handle(GetPostByIdQuery query) {
        return postRepository.findById(query.id());
    }

    @Override
    public List<Post> handle(GetAllPostsQuery query) {
        return postRepository.findAll();
    }

    @Override
    public List<Post> handle(GetPostsByCommunityIdQuery query) {
        return postRepositoryAdapter.findByCommunityId(query.communityId());
    }
}
