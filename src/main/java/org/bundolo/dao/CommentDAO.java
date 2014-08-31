package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.model.Comment;
import org.bundolo.model.Content;
import org.springframework.stereotype.Repository;

@Repository("commentDAO")
public class CommentDAO extends JpaDAO<Long, Comment> {

    private static final Logger logger = Logger.getLogger(CommentDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Comment> findCommentsByParentId(Long parentId) {
	List<Comment> result = null;
	if (parentId != null) {
	    String queryString = "SELECT c FROM Comment c WHERE content_status='active'";
	    queryString += " AND parent_content_id =" + parentId;
	    queryString += " AND kind like '%comment%'";
	    queryString += " ORDER BY creationDate";
	    logger.log(Level.WARNING, "queryString: " + queryString);
	    Query q = entityManager.createQuery(queryString);
	    result = q.getResultList();
	}
	return result;
    }

    @SuppressWarnings("unchecked")
    public List<Comment> findCommentsWithParents(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Comment c WHERE kind like '%comment%' AND content_status='active'");
	if (ArrayUtils.isNotEmpty(filterBy)) {
	    String prefix = " AND LOWER(";
	    String suffix = ") LIKE '%";
	    String postfix = "%'";
	    for (int i = 0; i < filterBy.length; i++) {
		queryString.append(prefix);
		queryString.append(filterBy[i]);
		queryString.append(suffix);
		queryString.append(filter[i].toLowerCase());
		queryString.append(postfix);
	    }
	    // TODO consider making these links functional by keeping old ids in database
	    // avoid old links
	    queryString.append("AND content_text NOT LIKE '%http://www.bundolo.org/%'");
	    queryString.append("AND content_text NOT LIKE '%http://bundolo.org/%'");
	}
	if (ArrayUtils.isNotEmpty(orderBy) && ArrayUtils.isSameLength(orderBy, order)) {
	    String firstPrefix = " ORDER BY ";
	    String nextPrefix = ", ";
	    String prefix = firstPrefix;
	    String suffix = " ";
	    for (int i = 0; i < orderBy.length; i++) {
		queryString.append(prefix);
		queryString.append(orderBy[i]);
		queryString.append(suffix);
		queryString.append(order[i]);
		prefix = nextPrefix;
	    }
	}
	logger.log(Level.WARNING, "queryString: " + queryString.toString() + ", start: " + start + ", max results: "
		+ (end - start + 1));
	Query q = entityManager.createQuery(queryString.toString());
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);

	// go up and return parents
	// TODO make sure that parents do not hold comments, we are not using them
	List<Comment> comments = q.getResultList();
	// logger.log(Level.WARNING, "comments: " + comments);
	for (Comment comment : comments) {
	    // logger.log(Level.WARNING, "comment: " + comment);
	    Content commentAncestor = comment.getParentContent();
	    while (commentAncestor.getParentContent() != null) {
		commentAncestor = commentAncestor.getParentContent();
	    }
	    // logger.log(Level.WARNING, "commentAncestor: " + commentAncestor);

	    comment.setParentContent(commentAncestor);
	}
	return comments;
    }

}