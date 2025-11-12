package com.levelup.journey.platform.post.application.internal.queryservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
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

    public PostQueryServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> handle(GetPostByIdQuery query) {
        return postRepository.findById(query.id());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetAllPostsQuery query) {
        // Use paginated repository method (sorting is handled by MongoDB query)
        return postRepository.findAll(query.page(), query.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetPostsByCommunityIdQuery query) {
        // Use paginated repository method (sorting is handled by MongoDB query)
        return postRepository.findByCommunityId(query.communityId(), query.page(), query.size());
    }
}
