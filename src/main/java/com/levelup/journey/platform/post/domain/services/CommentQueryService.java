package com.levelup.journey.platform.post.domain.services;

import com.levelup.journey.platform.post.domain.model.entities.Comment;
import com.levelup.journey.platform.post.domain.model.queries.GetCommentsByPostIdQuery;

import java.util.List;

/**
 * Comment Query Service
 * Defines read operations for comments
 */
public interface CommentQueryService {

    /**
     * Handles the query to get all comments for a specific post
     * @param query the get comments by post id query
     * @return list of comments ordered by creation time (newest first)
     */
    List<Comment> handle(GetCommentsByPostIdQuery query);
}
