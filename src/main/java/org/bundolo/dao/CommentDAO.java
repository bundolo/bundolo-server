package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.Comment;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("commentDAO")
public class CommentDAO extends JpaDAO<Long, Comment> {

    private static final Logger logger = Logger.getLogger(CommentDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Comment> findCommentsByParentId(Long parentId) {
	List<Comment> result = null;
	if (parentId != null) {

	    // clear() forces reading from database instead of cache
	    // it can be optimised to clear only when needed
	    Session session = entityManager.unwrap(Session.class);
	    session.clear();
	    String queryString = "SELECT c FROM Comment c WHERE content_status='active'";
	    queryString += " AND parent_content_id =" + parentId;
	    queryString += " ORDER BY creationDate";
	    logger.log(Level.WARNING, "queryString: " + queryString);
	    Query q = entityManager.createQuery(queryString);
	    result = q.getResultList();
	}
	return result;
    }

}