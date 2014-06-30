package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.model.Contest;
import org.bundolo.model.enumeration.ContentKindType;
import org.springframework.stereotype.Repository;

@Repository("contestDAO")
public class ContestDAO extends JpaDAO<Long, Contest> {

    private static final Logger logger = Logger.getLogger(ContestDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<Contest> findContests(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT c FROM Contest c WHERE contest_status='active'");
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
    public Contest findByTitle(String title) {
	String queryString = "SELECT c1 FROM Contest c1, Content c2";
	queryString += " WHERE c2.kind = '" + ContentKindType.contest_description + "'";
	queryString += " AND c2.name " + ((title == null) ? "IS NULL" : "='" + title + "'");
	queryString += " AND c1.contestStatus='active'";
	queryString += " AND c1.descriptionContent.contentId=c2.contentId";
	logger.log(Level.WARNING, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<Contest> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

}