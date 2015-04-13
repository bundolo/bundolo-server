package org.bundolo.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.UserProfile;
import org.springframework.stereotype.Repository;

@Repository("userProfileDAO")
public class UserProfileDAO extends JpaDAO<Long, UserProfile> {

    private static final Logger logger = Logger.getLogger(UserProfileDAO.class.getName());

    @SuppressWarnings("unchecked")
    public UserProfile findByField(final String field, final String value) {
	String queryString = "SELECT u FROM " + entityClass.getName() + " u";
	queryString += " WHERE " + field + " " + ((value == null) ? "IS NULL" : "='" + value + "'");
	queryString += " AND (user_profile_status='active' OR user_profile_status='pending')";
	logger.log(Level.FINE, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setMaxResults(1);
	List<UserProfile> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return resultList.get(0);
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public List<UserProfile> findNewsletterUsers(Date sendingStart, Integer maxResults) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT u FROM " + entityClass.getName() + " u");
	queryString.append(" WHERE subscribed");
	queryString.append(" AND email LIKE '%_@__%.__%'");
	queryString.append(" AND newsletter_sending_date IS NOT NULL");
	queryString.append(" AND newsletter_sending_date < ?");
	logger.log(Level.WARNING, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, sendingStart);
	q.setMaxResults(maxResults);
	List<UserProfile> resultList = q.getResultList();
	return resultList;
    }

    public long dailyRecipientsCount(Date day) {
	String queryString = "SELECT count(u) FROM " + entityClass.getName() + " u";
	queryString += " WHERE subscribed";
	queryString += " AND ((newsletter_sending_date >= ?::timestamp::date and newsletter_sending_date < ?::timestamp::date + interval '1 day')";
	queryString += " OR (newsletter_sending_date IS NULL))";
	logger.log(Level.WARNING, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	long count = (long) q.getSingleResult();
	return count;
    }

    public long dailyUndeliverablesCount() {
	String queryString = "SELECT count(u) FROM " + entityClass.getName() + " u";
	queryString += " WHERE subscribed AND newsletter_sending_date IS NULL";
	logger.log(Level.WARNING, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	long count = (long) q.getSingleResult();
	return count;
    }

    public void unsubscribeUndeliverables() {
	String queryString = "UPDATE " + entityClass.getName();
	queryString += " SET subscribed = false";
	queryString += ", newsletter_sending_date = to_timestamp(0)";
	queryString += " WHERE subscribed AND newsletter_sending_date IS NULL";
	logger.log(Level.WARNING, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.executeUpdate();
    }

}