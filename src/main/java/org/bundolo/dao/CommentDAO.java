package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.model.Comment;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.springframework.stereotype.Repository;

@Repository("commentDAO")
public class CommentDAO extends JpaDAO<Long, Comment> {

	private static final Logger logger = Logger.getLogger(CommentDAO.class.getName());

	@SuppressWarnings("unchecked")
	public List<Comment> findCommentsByParentId(Long parentId) {
		List<Comment> result = null;
		if (parentId != null) {
			String queryString = "SELECT c FROM Comment c WHERE content_status='active'";
			queryString += " AND parent_content_id =?1";
			queryString += " AND kind like '%comment%'";
			queryString += " ORDER BY creationDate";
			logger.log(Level.INFO, "queryString: " + queryString);
			Query q = entityManager.createQuery(queryString);
			q.setParameter(1, parentId);
			result = q.getResultList();
			for (Comment comment : result) {
				comment.setParentContent(null);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Comment> findCommentsWithParents(Integer start, Integer end, String[] orderBy, String[] order,
			String[] filterBy, String[] filter) {
		int filterParamCounter = 0;
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT c FROM Comment c WHERE c.kind like '%comment%' AND c.contentStatus='active'");

		// do not retrieve comments on personal item lists
		queryString.append(
				" AND ((c.kind = 'item_list_comment' AND (select i.kind from ItemList i where c.ancestorContent.contentId = i.descriptionContent.contentId and (i.kind='elected' OR i.kind='general' OR i.kind='named')) is not null )OR (c.kind <> 'item_list_comment')) ");

		if (ArrayUtils.isNotEmpty(filterBy)) {
			String prefix = " AND LOWER(";
			String suffix = ") LIKE '%";
			String postfix = "%'";
			for (int i = 0; i < filterBy.length; i++) {
				if ("to_char(ancestorContent.lastActivity, 'DD.MM.YYYY.')".equals(filterBy[i])) {
					// special case, if we are filtering by ancestor last
					// activity, it means we want comments from
					// distinct ancestors
					queryString.append(" AND c.creationDate = c.ancestorContent.lastActivity");
				} else {
					queryString.append(prefix);
					queryString.append(filterBy[i]);
					queryString.append(suffix);
					filterParamCounter++;
					queryString.append("'||?" + filterParamCounter + "||'");
					queryString.append(postfix);
				}
			}
			// avoid old links
			// TODO consider making these links functional by keeping old ids in
			// database
			// queryString.append(" AND c.text NOT LIKE
			// '%http://www.bundolo.org/%'");
			// queryString.append(" AND c.text NOT LIKE
			// '%http://bundolo.org/%'");
			// queryString.append(" AND c.text NOT LIKE
			// '%http://bundolo.f2o.org/%'");
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
		logger.log(Level.WARNING,
				"queryString: " + queryString.toString() + ", start: " + start + ", max results: " + (end - start + 1));
		Query q = entityManager.createQuery(queryString.toString());
		if (filterParamCounter > 0) {
			for (int i = 0; i < filterBy.length; i++) {
				q.setParameter(i + 1, filter[i].toLowerCase());
			}
		}
		q.setFirstResult(start);
		q.setMaxResults(end - start + 1);

		// go up and return parents
		// TODO make sure that parents do not hold comments, we are not using
		// them
		// TODO we can use ancestor content instead of this
		List<Comment> comments = q.getResultList();
		// logger.log(Level.INFO, "comments: " + comments);
		for (Comment comment : comments) {
			// logger.log(Level.INFO, "comment: " + comment);
			Content commentAncestor = comment.getParentContent();
			while (!ContentKindType.episode.equals(commentAncestor.getKind())
					&& commentAncestor.getParentContent() != null) {
				commentAncestor = commentAncestor.getParentContent();
			}
			// strip parent to the minimum that is going to be used, to make the
			// request run faster
			commentAncestor.setRating(null);
			if (!ContentKindType.page_description.equals(commentAncestor.getKind())) {
				commentAncestor.setText("");
			}
			// logger.log(Level.INFO, "commentAncestor: " + commentAncestor);
			comment.setParentContent(commentAncestor);
		}
		return comments;
	}

}