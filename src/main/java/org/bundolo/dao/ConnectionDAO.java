package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.model.Connection;
import org.bundolo.model.enumeration.ContentKindType;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository("connectionDAO")
public class ConnectionDAO extends JpaDAO<Long, Connection> {

    private static final Logger logger = Logger.getLogger(ConnectionDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Connection> findConnections(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Connection c WHERE connection_status='active'");
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
    public Connection findByTitle(String title) {
	String queryString = "SELECT c1 FROM Connection c1, Content c2";
	queryString += " WHERE c2.kind = '" + ContentKindType.connection_description + "'";
	queryString += " AND c2.name " + ((title == null) ? "IS NULL" : "='" + title + "'");
	queryString += " AND c1.connectionStatus='active'";
	queryString += " AND c1.descriptionContent.contentId=c2.contentId";
	logger.log(Level.WARNING, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Connection> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public Connection findNext(Long connectionId, String orderBy, String fixBy, boolean ascending) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c1 FROM Connection c1, Connection c2");
	queryString.append(" WHERE c2.connectionId = " + connectionId);
	queryString.append(" AND c1.connectionStatus='active'");
	if (StringUtils.hasText(fixBy)) {
	    queryString.append(" AND c1." + fixBy + "=c2." + fixBy);
	}
	queryString.append(" AND c1." + orderBy + (ascending ? ">" : "<") + "c2." + orderBy);
	queryString.append(" ORDER BY c1." + orderBy + " " + (ascending ? "ASC" : "DESC"));
	logger.log(Level.WARNING, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	q.setMaxResults(1);
	List<Connection> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }
}