package com.levelup.journey.platform.community.post.application.internal.queryservices;

import com.levelup.journey.platform.community.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.community.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.community.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.community.post.domain.model.queries.GetPostsByUserIdQuery;
import com.levelup.journey.platform.community.post.domain.services.PostQueryService;
import com.levelup.journey.platform.community.post.infrastructure.persistence.jpa.repositories.PostRepository;
import com.levelup.journey.platform.community.shared.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of PostQueryService.
 * Handles all read-only query operations for posts.
 */
@Service
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;

    public PostQueryServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Post> handle(GetPostByIdQuery query) {
        return postRepository.findByIdAndIsDeletedFalse(query.postId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetAllPostsQuery query) {
        return postRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetPostsByUserIdQuery query) {
        var userId = new UserId(query.userId());
        return postRepository.findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }
}
