package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.PageKindType;
import org.springframework.stereotype.Repository;

@Repository("contentDAO")
public class ContentDAO extends JpaDAO<Long, Content> {

    private static final Logger logger = Logger.getLogger(ContentDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Content> findTexts(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE kind='text' AND content_status='active'");
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
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findAnnouncements(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE kind='news' AND content_status='active'");
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
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findSerials(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE kind='episode_group' AND content_status='active'");
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
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findTopics(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE kind='forum_topic' AND content_status='active'");
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
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Content getPageDescriptionContent(PageKindType pageKind) {
	Content result = null;
	if (pageKind != null) {
	    String queryString = "SELECT c FROM Content c, Page p";
	    queryString += " WHERE p.kind ='" + pageKind + "'";
	    queryString += " AND p.descriptionContentId = c.contentId";
	    logger.log(Level.WARNING, "queryString: " + queryString);

	    Query q = entityManager.createQuery(queryString);
	    q.setMaxResults(1);
	    List<Content> resultList = q.getResultList();
	    if (resultList != null && resultList.size() > 0) {
		result = resultList.get(0);
	    }
	}
	return result;
    }

    @SuppressWarnings("unchecked")
    public Content findByTitle(String title, ContentKindType kind) {
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE kind = '" + kind + "'";
	queryString += " AND content_name " + ((title == null) ? "IS NULL" : "='" + title + "'");
	queryString += " AND content_status='active'";
	logger.log(Level.WARNING, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Content> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public Content findByText(String text, ContentKindType kind) {
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE kind = '" + kind + "'";
	queryString += " AND content_text " + ((text == null) ? "IS NULL" : "='" + text + "'");
	queryString += " AND content_status='active'";
	logger.log(Level.WARNING, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Content> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public Content findText(String username, String title) {
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE kind = '" + ContentKindType.text + "'";
	queryString += " AND author_username " + ((username == null) ? "IS NULL" : "='" + username + "'");
	queryString += " AND content_name " + ((title == null) ? "IS NULL" : "='" + title + "'");
	queryString += " AND content_status='active'";
	logger.log(Level.WARNING, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Content> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public List<Content> findConnectionGroups() {
	StringBuilder queryString = new StringBuilder();
	queryString
		.append("SELECT c FROM Content c WHERE kind='connection_group' AND content_status='active' ORDER BY creation_date ASC");
	logger.log(Level.WARNING, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findTopicGroups() {
	StringBuilder queryString = new StringBuilder();
	queryString
		.append("SELECT c FROM Content c WHERE kind='forum_group' AND content_status='active' ORDER BY creation_date ASC");
	logger.log(Level.WARNING, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	return q.getResultList();
    }
}