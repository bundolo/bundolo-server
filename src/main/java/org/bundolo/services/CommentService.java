package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Comment;
import org.bundolo.model.enumeration.ReturnMessageType;

public interface CommentService {

    public List<Comment> findCommentsByParentId(Long parentId);

    public List<Comment> findComments(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public ReturnMessageType saveComment(Comment comment);

    public void clearSession();

    public Boolean deleteCommentsByParentId(Long parentId);

}