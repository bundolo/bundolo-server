package org.bundolo.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.SecurityUtils;
import org.bundolo.dao.CommentDAO;
import org.bundolo.model.Comment;
import org.bundolo.model.enumeration.ContentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = Logger.getLogger(CommentServiceImpl.class.getName());

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
	comment.setContentStatus(ContentStatusType.active);
	comment.setLocale("sr");
	comment.setAuthorUsername(SecurityUtils.getUsername());
	commentDAO.persist(comment);
	result = comment.getContentId();
	logger.log(Level.WARNING, "++++++++saving comment after: " + result);
	return result;
    }

    @Override
    public void clearSession() {
	commentDAO.clear();
    }

    @Override
    public List<Comment> findComments(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	return commentDAO.findCommentsWithParents(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean deleteCommentsByParentId(Long parentId) {
	try {
	    List<Comment> comments = commentDAO.findCommentsByParentId(parentId);
	    for (Comment comment : comments) {
		comment.setContentStatus(ContentStatusType.disabled);
		commentDAO.merge(comment);
	    }
	    return true;
	} catch (Exception ex) {
	    return false;
	}
    }

}
