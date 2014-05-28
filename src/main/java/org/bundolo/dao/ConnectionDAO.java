package org.bundolo.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.Constants;
import org.bundolo.model.Connection;
import org.springframework.stereotype.Repository;

@Repository("connectionDAO")
public class ConnectionDAO extends JpaDAO<Long, Connection> {

    private static final Logger logger = Logger.getLogger(ConnectionDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Connection> findItemListConnections(final String queryString, final Integer start, final Integer end) {
	logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	return q.getResultList();
    }

    public Integer findItemListConnectionsCount(final String queryString) {
	String modifiedQueryString = queryString.replace("SELECT c", "SELECT COUNT(c)");
	int orderBySectionStart = modifiedQueryString.indexOf(" ORDER BY ");
	if (orderBySectionStart > -1) {
	    modifiedQueryString = modifiedQueryString.substring(0, orderBySectionStart);
	}
	logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "queryString: " + modifiedQueryString);

	Query q = entityManager.createQuery(modifiedQueryString);
	return (Integer) q.getSingleResult();
    }
}