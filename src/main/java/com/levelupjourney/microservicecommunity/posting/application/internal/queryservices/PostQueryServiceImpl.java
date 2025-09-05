package com.levelupjourney.microservicecommunity.posting.application.internal.queryservices;

import com.levelupjourney.microservicecommunity.posting.domain.model.aggregates.Post;
import com.levelupjourney.microservicecommunity.posting.domain.model.queries.GetPostFeedQuery;
import com.levelupjourney.microservicecommunity.posting.domain.model.queries.GetPostQuery;
import com.levelupjourney.microservicecommunity.posting.domain.services.PostQueryService;
import com.levelupjourney.microservicecommunity.posting.infrastructure.persistence.mongodb.repositories.PostRepository;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.PostId;
import com.levelupjourney.microservicecommunity.shared.domain.model.valueobjects.UserId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of PostQueryService.
 * Handles post query operations with read-only access.
 */
@Service
public class PostQueryServiceImpl implements PostQueryService {
    
    private final PostRepository postRepository;
    
    public PostQueryServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Post> handle(GetPostQuery query) {
        var postId = PostId.of(query.postId());
        return postRepository.findByPostId(postId)
            .filter(Post::isActive); // Only return active posts
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Post> handle(GetPostFeedQuery query) {
        if (query.hasAuthorFilter()) {
            var authorId = UserId.of(query.authorId());
            return postRepository.findByAuthorIdAndDeletedFalse(authorId, query.pageable());
        } else {
            return postRepository.findByDeletedFalse(query.pageable());
        }
    }
}
