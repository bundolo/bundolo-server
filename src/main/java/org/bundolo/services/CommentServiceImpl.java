package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.dao.CommentDAO;
import org.bundolo.model.Comment;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long saveComment(Comment comment) {
	Long result = null;
	// TODO check if comment exists
	comment.setCreationDate(new Date());
	comment.setContentStatus(ContentStatusType.active);
	comment.setKind(ContentKindType.text_comment);
	comment.setLocale("sr");
	commentDAO.persist(comment);
	result = comment.getContentId();
	return result;
    }

}
