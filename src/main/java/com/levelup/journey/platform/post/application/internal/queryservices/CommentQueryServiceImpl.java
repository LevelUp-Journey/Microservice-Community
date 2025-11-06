package com.levelup.journey.platform.post.application.internal.queryservices;

import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.queries.GetCommentsByPostIdQuery;
import com.levelup.journey.platform.post.domain.model.repositories.CommentRepository;
import com.levelup.journey.platform.post.domain.services.CommentQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Comment Query Service Implementation
 * Handles comment-related queries
 */
@Service
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;

    public CommentQueryServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> handle(GetCommentsByPostIdQuery query) {
        return commentRepository.findByPostId(query.postId());
    }
}
