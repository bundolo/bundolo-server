package org.bundolo.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.UserProfile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	queryString.append(" WHERE subscribed = true");
	queryString.append(" AND email LIKE '%_@__%.__%'");
	queryString.append(" AND newsletter_sending_date IS NOT NULL");
	queryString.append(" AND newsletter_sending_date < ?1");
	logger.log(Level.FINE, "queryString: " + queryString.toString() + "; sendingStart: " + sendingStart
		+ "; maxResults: " + maxResults);
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, sendingStart);
	if (maxResults > 0) {
	    q.setMaxResults(maxResults);
	}
	List<UserProfile> resultList = q.getResultList();

	logger.log(Level.FINE, "resultList: " + resultList.size());
	return resultList;
    }

    public long dailyRecipientsCount(Date day) {
	// todo get midnight day
	// add one day
	Calendar dayMidnight = Calendar.getInstance();
	dayMidnight.setTime(day);
	dayMidnight.set(Calendar.HOUR_OF_DAY, 0);
	dayMidnight.set(Calendar.MINUTE, 0);
	dayMidnight.set(Calendar.SECOND, 0);
	dayMidnight.set(Calendar.MILLISECOND, 0);

	Calendar nextDayMidnight = (Calendar) dayMidnight.clone();
	nextDayMidnight.add(Calendar.DATE, 1);

	String queryString = "SELECT count(u) FROM " + entityClass.getName() + " u";
	queryString += " WHERE subscribed = true";
	queryString += " AND ((newsletter_sending_date BETWEEN ?1 and ?2)";
	queryString += " OR (newsletter_sending_date IS NULL))";
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.setParameter(1, dayMidnight);
	q.setParameter(2, nextDayMidnight);
	long count = (long) q.getSingleResult();

	logger.log(Level.FINE, "dailyRecipientsCount: " + count);
	return count;
    }

    public long dailyUndeliverablesCount() {
	String queryString = "SELECT count(u) FROM " + entityClass.getName() + " u";
	queryString += " WHERE subscribed = true AND newsletter_sending_date IS NULL";
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	long count = (long) q.getSingleResult();

	logger.log(Level.FINE, "dailyUndeliverablesCount: " + count);
	return count;
    }

    public void unsubscribeUndeliverables() {
	String queryString = "UPDATE " + entityClass.getName();
	queryString += " SET subscribed = false";
	queryString += ", newsletter_sending_date = to_timestamp(0)";
	queryString += " WHERE subscribed = true AND newsletter_sending_date IS NULL";
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.executeUpdate();
    }

    // used only during testing
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void resetSubscribers() {
	String queryString = "update " + entityClass.getName() + " set subscribed=true where email LIKE '%_@__%.__%'";
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.executeUpdate();
	String queryString1 = "update " + entityClass.getName()
		+ " set newsletter_sending_date=signup_date where subscribed = true";
	logger.log(Level.FINE, "queryString: " + queryString1);
	Query q1 = entityManager.createQuery(queryString1);
	q1.executeUpdate();
    }

    // used only during testing
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unsubscribeAll() {
	String queryString = "update " + entityClass.getName() + " set subscribed=false";
	Query q = entityManager.createQuery(queryString);
	q.executeUpdate();
    }

}