package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.Contest;
import org.springframework.stereotype.Repository;

@Repository("contestDAO")
public class ContestDAO extends JpaDAO<Long, Contest> {

    private static final Logger logger = Logger.getLogger(ContestDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Contest> findItemListContests(final String queryString, final Integer start, final Integer end) {
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	return q.getResultList();
    }

    public Integer findItemListContestsCount(final String queryString) {
	String modifiedQueryString = queryString.replace("SELECT c", "SELECT COUNT(c)");
	int orderBySectionStart = modifiedQueryString.indexOf(" ORDER BY ");
	if (orderBySectionStart > -1) {
	    modifiedQueryString = modifiedQueryString.substring(0, orderBySectionStart);
	}
	logger.log(Level.FINE, "queryString: " + modifiedQueryString);

	Query q = entityManager.createQuery(modifiedQueryString);
	return (Integer) q.getSingleResult();
    }

}