package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.Page;
import org.springframework.stereotype.Repository;

@Repository("pageDAO")
public class PageDAO extends JpaDAO<Long, Page> {

    private static final Logger logger = Logger.getLogger(PageDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Page> findPages(final Long parentPageId, final boolean justActive) {
	logger.log(Level.FINE, "findPages parentPageId: " + parentPageId + ", justActive: " + justActive);
	String queryString = "SELECT p FROM " + entityClass.getName() + " p";
	queryString += " WHERE parentPageId " + ((parentPageId == null) ? "IS NULL" : "=" + parentPageId);
	if (justActive) {
	    queryString += " AND pageStatus = 'active'";
	}
	queryString += " ORDER BY displayOrder";
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	return q.getResultList();
    }

    public Integer findPagesCount(final Long parentPageId) {
	String queryString = "SELECT COUNT(p) FROM " + entityClass.getName() + " p";
	queryString += " WHERE parentPageId " + ((parentPageId == null) ? "IS NULL" : "=" + parentPageId);
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	return (Integer) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public Page findNextPage(final Long previousPageId) {
	String queryString = "SELECT p FROM " + entityClass.getName() + " p";
	if (previousPageId != null) {
	    Page previousPage = findById(previousPageId);
	    if (previousPage != null) {
		queryString += " WHERE p.parentPageId "
			+ ((previousPage.getParentPageId() == null) ? "IS NULL" : "= " + previousPage.getParentPageId());
		queryString += " AND p.displayOrder > " + previousPage.getDisplayOrder();
	    } else {
		queryString += " WHERE p.parentPageId IS NULL";
	    }
	} else {
	    queryString += " WHERE p.parentPageId IS NULL";
	}
	queryString += " ORDER BY p.displayOrder";
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Page> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public Page findHomePage() {
	logger.log(Level.FINE, "findHomePage");
	String queryString = "SELECT p FROM " + entityClass.getName() + " p";
	queryString += " WHERE p.parentPageId IS NULL";
	queryString += " ORDER BY p.displayOrder";
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Page> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

}