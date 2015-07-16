package org.bundolo.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.PageKindType;
import org.springframework.stereotype.Repository;

@Repository("contentDAO")
public class ContentDAO extends JpaDAO<Long, Content> {

    private static final Logger logger = Logger.getLogger(ContentDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Content> findTexts(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	int filterParamCounter = 0;
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
		filterParamCounter++;
		queryString.append("'||?" + filterParamCounter + "||'");
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
	logger.log(Level.INFO, "queryString: " + queryString.toString() + ", start: " + start + ", max results: "
		+ (end - start + 1));
	Query q = entityManager.createQuery(queryString.toString());
	if (filterParamCounter > 0) {
	    for (int i = 0; i < filterBy.length; i++) {
		q.setParameter(i + 1, filter[i].toLowerCase());
	    }
	}
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	// strip text to make the request run faster
	List<Content> texts = q.getResultList();
	for (Content text : texts) {
	    text.setText("");
	    text.setRating(null);
	}
	return texts;
    }

    @SuppressWarnings("unchecked")
    public List<Content> findAnnouncements(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	int filterParamCounter = 0;
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
		filterParamCounter++;
		queryString.append("'||?" + filterParamCounter + "||'");
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
	logger.log(Level.INFO, "queryString: " + queryString.toString() + ", start: " + start + ", max results: "
		+ (end - start + 1));
	Query q = entityManager.createQuery(queryString.toString());
	if (filterParamCounter > 0) {
	    for (int i = 0; i < filterBy.length; i++) {
		q.setParameter(i + 1, filter[i].toLowerCase());
	    }
	}
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findSerials(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	int filterParamCounter = 0;
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE kind='episode_group'");
	queryString.append(" AND (content_status='active' OR content_status='pending')");
	if (ArrayUtils.isNotEmpty(filterBy)) {
	    String prefix = " AND LOWER(";
	    String suffix = ") LIKE '%";
	    String postfix = "%'";
	    for (int i = 0; i < filterBy.length; i++) {
		queryString.append(prefix);
		queryString.append(filterBy[i]);
		queryString.append(suffix);
		filterParamCounter++;
		queryString.append("'||?" + filterParamCounter + "||'");
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
	logger.log(Level.INFO, "queryString: " + queryString.toString() + ", start: " + start + ", max results: "
		+ (end - start + 1));
	Query q = entityManager.createQuery(queryString.toString());
	if (filterParamCounter > 0) {
	    for (int i = 0; i < filterBy.length; i++) {
		q.setParameter(i + 1, filter[i].toLowerCase());
	    }
	}
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findTopics(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	int filterParamCounter = 0;
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE kind='forum_topic'");
	queryString.append(" AND contentStatus='active'");
	// TODO archived topics are not visible. if we enable retrieving disabled content, we have to handle exceptions
	// in editing, adding, comments...
	// queryString
	// .append(" AND (contentStatus='active' OR (parentContent.name='arhiva' AND contentStatus='disabled'))");
	if (ArrayUtils.isNotEmpty(filterBy)) {
	    String prefix = " AND LOWER(";
	    String suffix = ") LIKE '%";
	    String postfix = "%'";
	    for (int i = 0; i < filterBy.length; i++) {
		queryString.append(prefix);
		queryString.append(filterBy[i]);
		queryString.append(suffix);
		filterParamCounter++;
		queryString.append("'||?" + filterParamCounter + "||'");
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
	logger.log(Level.INFO, "queryString: " + queryString.toString() + ", start: " + start + ", max results: "
		+ (end - start + 1));
	Query q = entityManager.createQuery(queryString.toString());
	if (filterParamCounter > 0) {
	    for (int i = 0; i < filterBy.length; i++) {
		q.setParameter(i + 1, filter[i].toLowerCase());
	    }
	}
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
	    logger.log(Level.INFO, "queryString: " + queryString);

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
	logger.log(Level.INFO, "findByTitle: " + title + ", kind: " + kind);
	if (title == null) {
	    return null;
	}
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE kind = '" + kind + "'";
	queryString += " AND content_name =?1";
	if (ContentKindType.episode_group.equals(kind)) {
	    queryString += " AND (content_status='active' OR content_status='pending')";
	} else {
	    queryString += " AND content_status='active'";
	}
	logger.log(Level.INFO, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setParameter(1, title);
	q.setMaxResults(1);
	List<Content> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    Content result = resultList.get(0);
	    if (ContentKindType.forum_topic.equals(kind) || ContentKindType.episode.equals(kind)) {
		result.setParentGroup(result.getParentContent().getName());
	    }
	    return result;
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public Content findText(String username, String title) {
	if (username == null || title == null) {
	    return null;
	}
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE kind = '" + ContentKindType.text + "'";
	queryString += " AND author_username =?1";
	queryString += " AND content_name =?2";
	queryString += " AND content_status='active'";
	logger.log(Level.INFO, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setParameter(1, username);
	q.setParameter(2, title);
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
	logger.log(Level.INFO, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findTopicGroups() {
	StringBuilder queryString = new StringBuilder();
	queryString
		.append("SELECT c FROM Content c WHERE kind='forum_group' AND content_status='active' ORDER BY creation_date ASC");
	logger.log(Level.INFO, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findPosts(Long parentId, Integer start, Integer end) {
	if (parentId == null) {
	    return null;
	}
	String queryString = "SELECT c FROM Content c WHERE kind='forum_post' AND content_status='active'";
	queryString += " AND parent_content_id =?1";
	queryString += " ORDER BY creationDate";
	logger.log(Level.INFO, "queryString: " + queryString.toString() + ", start: " + start + ", max results: "
		+ (end - start + 1));
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, parentId);
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> findEpisodes(Long parentId, Integer start, Integer end) {
	if (parentId == null) {
	    return null;
	}
	String queryString = "SELECT c FROM Content c WHERE kind='episode' AND (content_status='active' OR content_status='pending')";
	queryString += " AND parent_content_id =?1";
	queryString += " ORDER BY creationDate";
	logger.log(Level.INFO, "queryString: " + queryString.toString() + ", start: " + start + ", max results: "
		+ (end - start + 1));
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, parentId);
	q.setFirstResult(start);
	if (end != null && end >= 0) {
	    q.setMaxResults(end - start + 1);
	}
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Content findEpisode(String serialTitle, String title) {
	if (serialTitle == null || title == null) {
	    return null;
	}
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE c.kind = '" + ContentKindType.episode + "'";
	queryString += " AND parentContent.name =?1";
	queryString += " AND c.name =?2";
	queryString += " AND (c.contentStatus='active' OR c.contentStatus='pending')";
	logger.log(Level.INFO, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setParameter(1, serialTitle);
	q.setParameter(2, title);
	q.setMaxResults(1);
	List<Content> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    Content result = resultList.get(0);
	    result.setParentGroup(result.getParentContent().getName());
	    return result;
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public List<Content> findStatistics(String username) {
	if (username == null) {
	    return null;
	}
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE author_username =?1";
	queryString += " AND (kind='" + ContentKindType.text + "' OR kind='" + ContentKindType.episode + "'";
	String senderUsername = SecurityUtils.getUsername();
	if (senderUsername != null) {
	    queryString += "  OR (author_username='" + senderUsername + "' AND kind='"
		    + ContentKindType.item_list_description + "')";
	}
	queryString += ")";
	queryString += " AND (content_status='active' OR content_status='pending')";
	queryString += " ORDER BY creationDate";
	logger.log(Level.INFO, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, username);
	List<Content> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    for (Content result : resultList) {
		if (ContentKindType.forum_topic.equals(result.getKind())
			|| ContentKindType.episode.equals(result.getKind()) && result.getParentContent() != null) {
		    result.setParentGroup(result.getParentContent().getName());
		}
	    }
	}
	return resultList;
    }

    @SuppressWarnings("unchecked")
    public Content findNext(Long contentId, String orderBy, String fixBy, boolean ascending) {
	if (contentId == null) {
	    return null;
	}
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c1 FROM Content c1, Content c2");
	queryString.append(" WHERE c2.contentId = ?1");
	queryString.append(" AND c1.kind = c2.kind");
	queryString.append(" AND c1.contentStatus='active'");
	if (StringUtils.isNotBlank(fixBy)) {
	    queryString.append(" AND c1." + fixBy + "=c2." + fixBy);
	}
	queryString.append(" AND c1." + orderBy + (ascending ? ">" : "<") + "c2." + orderBy);
	queryString.append(" ORDER BY c1." + orderBy + " " + (ascending ? "ASC" : "DESC"));
	logger.log(Level.INFO, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, contentId);
	q.setMaxResults(1);
	List<Content> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    Content result = resultList.get(0);
	    if (ContentKindType.forum_topic.equals(result.getKind())
		    || ContentKindType.episode.equals(result.getKind())) {
		result.setParentGroup(result.getParentContent().getName());
	    }
	    return result;
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public void disable(Content content) {
	String queryString = "SELECT c FROM Content c";
	queryString += " WHERE parent_content_id =?1";
	logger.log(Level.INFO, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.setParameter(1, content.getContentId());
	List<Content> children = q.getResultList();
	if (children != null && children.size() > 0) {
	    for (Content child : children) {
		disable(child);
	    }
	}
	content.setContentStatus(ContentStatusType.disabled);
	merge(content);
    }

    @SuppressWarnings("unchecked")
    public List<Content> findRecent(Date fromDate, Integer limit) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE content_status='active'");
	if (fromDate != null) {
	    queryString.append(" AND last_activity >=?");
	}
	queryString.append(" AND (kind='text' OR kind='forum_topic' OR kind='connection_description' OR kind='news' "
		+ "OR kind='contest_description' OR kind='episode' OR kind='user_description')");
	queryString.append(" ORDER BY last_activity desc");
	logger.log(Level.INFO, "queryString: " + queryString + ", fromDate: " + fromDate + ", limit: " + limit);
	Query q = entityManager.createQuery(queryString.toString());
	if (fromDate != null) {
	    q.setParameter(1, fromDate);
	}
	if (limit > 0) {
	    q.setMaxResults(limit);
	}
	// strip text to make the request run faster
	List<Content> recentContent = q.getResultList();
	for (Content content : recentContent) {
	    content.setText("");
	    content.setRating(null);
	    content.setDescription(null);
	    if (ContentKindType.episode.equals(content.getKind())) {
		content.setParentGroup(content.getParentContent().getName());
	    }
	}
	return recentContent;
    }

    @SuppressWarnings("unchecked")
    public List<Content> findItemListItems(String itemListIds) {
	if (StringUtils.isBlank(itemListIds)) {
	    return null;
	}
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Content c WHERE content_status='active'");
	queryString.append(" AND content_id IN " + itemListIds.replace("[", "(").replace("]", ")"));
	Query q = entityManager.createQuery(queryString.toString());
	// strip to make the request run faster
	List<Content> recentContent = q.getResultList();
	for (Content content : recentContent) {
	    if (!ContentKindType.page_description.equals(content.getKind())) {
		content.setText("");
	    }
	    content.setRating(null);
	    content.setDescription(null);
	    if (ContentKindType.episode.equals(content.getKind())) {
		content.setParentGroup(content.getParentContent().getName());
	    }
	}
	return recentContent;
    }
}