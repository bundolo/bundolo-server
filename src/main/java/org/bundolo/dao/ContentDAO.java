package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.Utils;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.PageKindType;
import org.springframework.stereotype.Repository;

@Repository("contentDAO")
public class ContentDAO extends JpaDAO<Long, Content> {

    private static final Logger logger = Logger.getLogger(ContentDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Content> findContents(final Long parentContentId, final ContentKindType kind, final String locale) {
	String queryString = "SELECT c FROM " + entityClass.getName() + " c";
	queryString += " WHERE parentContentId " + ((parentContentId == null) ? "IS NULL" : "=" + parentContentId);
	queryString += " AND kind " + ((kind == null) ? "IS NULL" : "='" + kind + "'");
	queryString += " AND locale " + ((locale == null) ? "IS NULL" : "='" + locale + "'");
	queryString += " ORDER BY creationDate";
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	return q.getResultList();
    }

    /*
     * get child content if it's the right locale. if not, get parent content in
     * the right locale. if not found, return null
     */
    @SuppressWarnings("unchecked")
    public Content findContentForLocale(final Long contentId, final ContentKindType kind, final String locale) {
	logger.log(Level.FINE, "locale: " + locale);
	Content result = null;
	if (contentId != null && Utils.hasText(locale)) {
	    result = findById(contentId);
	    if (result != null && !locale.equals(result.getLocale())) {
		String queryString = "SELECT c FROM " + entityClass.getName() + " c";
		queryString += " WHERE parentContentId =" + contentId;
		queryString += " AND kind " + ((kind == null) ? "IS NULL" : "='" + kind + "'");
		queryString += " AND locale ='" + locale + "'";
		logger.log(Level.FINE, "queryString: " + queryString);

		Query q = entityManager.createQuery(queryString);
		q.setMaxResults(1);
		List<Content> resultList = q.getResultList();
		if (resultList != null && resultList.size() > 0) {
		    return resultList.get(0);
		} else {
		    String queryStringChild = "SELECT c FROM " + entityClass.getName() + " c";
		    queryStringChild += " WHERE contentId =" + contentId;
		    queryString += " AND kind " + ((kind == null) ? "IS NULL" : "='" + kind + "'");
		    // queryStringChild += " AND locale ='" + locale +
		    // "'";
		    logger.log(Level.FINE, "queryStringChild: " + queryStringChild);

		    Query qChild = entityManager.createQuery(queryStringChild);
		    qChild.setMaxResults(1);
		    List<Content> resultListChild = qChild.getResultList();
		    if (resultListChild != null && resultListChild.size() > 0) {
			return resultListChild.get(0);
		    } else {
			return null;
		    }
		}
	    }
	}
	return result;
    }

    /*
     * get content by its name. kind and locale, without locale fallback. used
     * for labels at the moment
     */
    @SuppressWarnings("unchecked")
    public Content findContentForLocale(final String contentName, final ContentKindType kind, final String locale) {
	logger.log(Level.FINE, "locale: " + locale);
	Content result = null;
	if (Utils.hasText(contentName) && Utils.hasText(locale)) {
	    String queryString = "SELECT c FROM " + entityClass.getName() + " c";
	    queryString += " WHERE content_name ='" + contentName + "'";
	    queryString += " AND kind " + ((kind == null) ? "IS NULL" : "='" + kind + "'");
	    queryString += " AND locale ='" + locale + "'";
	    logger.log(Level.FINE, "queryString: " + queryString);

	    Query q = entityManager.createQuery(queryString);
	    q.setMaxResults(1);
	    List<Content> resultList = q.getResultList();
	    if (resultList != null && resultList.size() > 0) {
		return resultList.get(0);
	    } else {
		return null;
	    }
	}
	return result;
    }

    /*
     * Find content parent id based on content name
     */
    @SuppressWarnings("unchecked")
    public Long findParentContentId(final String contentName, final ContentKindType kind) {
	String queryString = "SELECT contentId FROM " + entityClass.getName() + " c";
	queryString += " WHERE content_name ='" + contentName + "'";
	queryString += " AND kind " + ((kind == null) ? "IS NULL" : "='" + kind + "'");
	queryString += " AND parentContentId IS NULL";
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Long> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public String getDefaultLocale() {
	String queryString = "SELECT DISTINCT(c.locale) FROM " + entityClass.getName() + " c";
	queryString += " WHERE parentContentId IS NULL";
	queryString += " and kind='label'";
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<String> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public List<String> getNonDefaultLocales() {
	String queryString = "SELECT DISTINCT(c.locale) FROM " + entityClass.getName() + " c";
	queryString += " WHERE parentContentId IS NOT NULL";
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<String> getLocales() {
	String queryString = "SELECT DISTINCT(c.locale) FROM " + entityClass.getName() + " c";
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Content> getLabelsForLocale(final String locale) {

	// idea is to get all entries translated for this locale joined with
	// default language entries for ones that haven't been translated
	// since hibernate does not support union and except, query does not
	// work
	// list joining was used instead of union, "not in" is used instead of
	// "except"
	// native query was one of the options as well
	// select c.* from content c where c.parent_page_id is null and
	// c.parent_content_id is not null and c.locale='sr'
	// union all (select c.* from content c where c.parent_page_id is null
	// and c.parent_content_id is null except all
	// select c1.* from content c, content c1 where c.parent_page_id is null
	// and c.parent_content_id is not null and c.locale='sr' and
	// c1.content_id=c.parent_content_id);

	String queryString1 = "SELECT c1 FROM " + entityClass.getName() + " c1";
	queryString1 += " WHERE c1.parentContentId IS NOT NULL";
	queryString1 += " AND c1.kind = 'label'";
	queryString1 += " AND c1.locale ='" + locale + "'";
	logger.log(Level.FINE, "queryString: " + queryString1);

	Query q1 = entityManager.createQuery(queryString1);

	String queryString2 = "SELECT c FROM " + entityClass.getName() + " c";
	queryString2 += " WHERE c.parentContentId IS NULL";
	queryString2 += " AND c.kind = 'label'";
	queryString2 += " AND c.contentId NOT IN(";
	queryString2 += " SELECT c1.parentContentId FROM " + entityClass.getName() + " c1";
	queryString2 += " WHERE c1.parentContentId IS NOT NULL";
	queryString2 += " AND c1.kind = 'label'";
	queryString2 += " AND c1.locale ='" + locale + "')";
	logger.log(Level.FINE, "queryString: " + queryString2);

	Query q2 = entityManager.createQuery(queryString2);

	List<Content> res = q1.getResultList();
	res.addAll(q2.getResultList());
	return res;
    }

    @SuppressWarnings("unchecked")
    public List<Content> findItemListContents(final String queryString, final Integer start, final Integer end) {
	// String modifiedQueryString = queryString + " LIMIT " + (end -
	// start + 1) + " OFFSET " + start;
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Integer findItemListContentsCount(final String queryString) {
	String modifiedQueryString = queryString.replace("SELECT c", "SELECT COUNT(c)");
	int orderBySectionStart = modifiedQueryString.indexOf(" ORDER BY ");
	if (orderBySectionStart > -1) {
	    modifiedQueryString = modifiedQueryString.substring(0, orderBySectionStart);
	}
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(modifiedQueryString);
	return (Integer) q.getSingleResult();
    }

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

}