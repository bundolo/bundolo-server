package org.bundolo.services;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.dao.CommentDAO;
import org.bundolo.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = Logger.getLogger(UserProfileServiceImpl.class.getName());

    @Autowired
    private CommentDAO commentDAO;

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    public List<Comment> findCommentsByParentId(Long parentId) {
	return commentDAO.findCommentsByParentId(parentId);
    }

}
