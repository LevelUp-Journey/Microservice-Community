package com.levelup.journey.platform.post.application.internal.queryservices;

import com.levelup.journey.platform.post.domain.model.aggregates.Post;
import com.levelup.journey.platform.post.domain.model.queries.GetAllPostsQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetCommentsByPostIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostByIdQuery;
import com.levelup.journey.platform.post.domain.model.queries.GetPostsByCommunityIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.PostRepository;
import com.levelup.journey.platform.post.domain.services.CommentQueryService;
import com.levelup.journey.platform.post.domain.services.PostQueryService;
import com.levelup.journey.platform.post.infrastructure.persistence.mongo.adapters.PostRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Post Query Service Implementation
 * Handles queries for Post aggregate
 * Loads comments separately from CommentQueryService
 */
@Service
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final CommentQueryService commentQueryService;

    public PostQueryServiceImpl(PostRepository postRepository,
                                PostRepositoryAdapter postRepositoryAdapter,
                                CommentQueryService commentQueryService) {
        this.postRepository = postRepository;
        this.postRepositoryAdapter = postRepositoryAdapter;
        this.commentQueryService = commentQueryService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> handle(GetPostByIdQuery query) {
        Optional<Post> postOptional = postRepository.findById(query.id());

        if (postOptional.isEmpty()) {
            return Optional.empty();
        }

        Post post = postOptional.get();
        var comments = commentQueryService.handle(new GetCommentsByPostIdQuery(post.id()));

        // Restore post with loaded comments
        Post postWithComments = Post.restore(
                post.id(),
                post.communityId(),
                post.authorId(),
                post.authorProfileId(),
                post.title(),
                post.content(),
                post.imageUrl(),
                post.createdAt(),
                comments
        );

        return Optional.of(postWithComments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetAllPostsQuery query) {
        List<Post> posts = postRepository.findAll();

        // Sort by creation date descending (most recent first)
        posts.sort((p1, p2) -> p2.createdAt().compareTo(p1.createdAt()));

        // Load comments for each post
        return posts.stream()
                .map(post -> {
                    var comments = commentQueryService.handle(new GetCommentsByPostIdQuery(post.id()));
                    return Post.restore(
                            post.id(),
                            post.communityId(),
                            post.authorId(),
                            post.authorProfileId(),
                            post.title(),
                            post.content(),
                            post.imageUrl(),
                            post.createdAt(),
                            comments
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> handle(GetPostsByCommunityIdQuery query) {
        List<Post> posts = postRepositoryAdapter.findByCommunityId(query.communityId());

        // Sort by creation date descending (most recent first)
        posts.sort((p1, p2) -> p2.createdAt().compareTo(p1.createdAt()));

        // Load comments for each post
        return posts.stream()
                .map(post -> {
                    var comments = commentQueryService.handle(new GetCommentsByPostIdQuery(post.id()));
                    return Post.restore(
                            post.id(),
                            post.communityId(),
                            post.authorId(),
                            post.authorProfileId(),
                            post.title(),
                            post.content(),
                            post.imageUrl(),
                            post.createdAt(),
                            comments
                    );
                })
                .collect(Collectors.toList());
    }
}
