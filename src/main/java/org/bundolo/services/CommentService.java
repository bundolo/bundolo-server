package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Comment;

public interface CommentService {

    public List<Comment> findCommentsByParentId(Long parentId);

    public Long saveComment(Comment comment);

    public void clearSession();

}