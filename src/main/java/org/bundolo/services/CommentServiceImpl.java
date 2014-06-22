package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.dao.CommentDAO;
import org.bundolo.model.Comment;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = Logger.getLogger(CommentServiceImpl.class.getName());

    @Autowired
    private CommentDAO commentDAO;

    // @Autowired
    // private ContentDAO contentDAO;

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
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	// TODO this is probably not safe enough. somebody could send some dummy username. check user role instead
	if (StringUtils.isNotBlank((String) authentication.getPrincipal())) {
	    comment.setAuthorUsername((String) authentication.getPrincipal());
	} else {
	    comment.setAuthorUsername(null);
	}
	logger.log(Level.WARNING, "++++++++saving comment: " + SecurityContextHolder.getContext().getAuthentication());

	commentDAO.persist(comment);
	result = comment.getContentId();
	return result;
    }

    @Override
    public void clearSession() {
	commentDAO.clear();
    }

}
