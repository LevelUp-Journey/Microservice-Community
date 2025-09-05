package com.levelupjourney.microservicecommunity.interaction.application.internal.queryservices;

import com.levelupjourney.microservicecommunity.interaction.domain.model.aggregates.Comment;
import com.levelupjourney.microservicecommunity.interaction.domain.services.CommentQueryService;
import com.levelupjourney.microservicecommunity.interaction.infrastructure.persistence.mongodb.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of comment query service.
 */
@Service
public class CommentQueryServiceImpl implements CommentQueryService {
    
    private final CommentRepository commentRepository;
    
    public CommentQueryServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    @Override
    public Optional<Comment> findById(String commentId) {
        return commentRepository.findById(commentId);
    }
    
    @Override
    public List<Comment> findByPostId(String postId) {
        return commentRepository.findByPostIdAndDeletedFalseOrderByCreatedAtAsc(postId);
    }
}
