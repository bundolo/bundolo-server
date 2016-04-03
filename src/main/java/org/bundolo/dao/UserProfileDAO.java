package org.bundolo.dao;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;
import org.bundolo.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("userProfileDAO")
public class UserProfileDAO extends JpaDAO<Long, UserProfile> {

    private static final Logger logger = Logger.getLogger(UserProfileDAO.class.getName());

    @Autowired
    @Qualifier("properties")
    private Properties properties;

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
    public List<UserProfile> findNewsletterUsers(Date sendingStart, Date bulletinDate, Integer maxResults) {
	Date lastSendingDaily = DateUtils.addDays(sendingStart, -1);
	Date lastSendingWeekly = DateUtils.addWeeks(sendingStart, -1);
	Date lastSendingMonthly = DateUtils.addMonths(sendingStart, -1);

	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT u FROM " + entityClass.getName() + " u");
	queryString.append(" WHERE newsletter_sending_date IS NOT NULL");
	queryString.append(" AND ((newsletter_subscriptions like '%\"daily\"%' AND newsletter_sending_date <= ?3)");
	queryString.append(" OR (newsletter_subscriptions like '%\"weekly\"%' AND newsletter_sending_date <= ?4)");
	queryString.append(" OR (newsletter_subscriptions like '%\"monthly\"%' AND newsletter_sending_date >= ?5)");
	queryString
		.append(" OR (newsletter_subscriptions like '%\"bulletin\"%' AND newsletter_sending_date < ?2 AND ?1 > ?2))");
	queryString.append(" AND email LIKE '%_@__%.__%'");
	logger.log(Level.FINE, "queryString: " + queryString.toString() + "; sendingStart: " + sendingStart
		+ "; maxResults: " + maxResults);
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, sendingStart);
	q.setParameter(2, bulletinDate);
	q.setParameter(3, lastSendingDaily);
	q.setParameter(4, lastSendingWeekly);
	q.setParameter(5, lastSendingMonthly);
	if (maxResults > 0) {
	    q.setMaxResults(maxResults);
	}
	List<UserProfile> resultList = q.getResultList();

	logger.log(Level.FINE, "resultList: " + resultList.size());
	return resultList;
    }

    public void updateNewsletterSendingDate(List<UserProfile> users, Date newsletterSendingDate) {
	StringBuilder userIds = new StringBuilder();
	String userIdsPrefix = "";
	for (UserProfile user : users) {
	    // compose comma separated list of user ids
	    userIds.append(userIdsPrefix);
	    userIdsPrefix = ", ";
	    userIds.append(user.getUserId());
	}
	StringBuilder queryString = new StringBuilder();
	queryString.append("UPDATE user_profile set newsletter_sending_date = ?1 where user_id in (" + userIds + ")");
	logger.log(Level.INFO, "queryString: " + queryString.toString() + "; newsletterSendingDate: "
		+ newsletterSendingDate);
	Query q = entityManager.createNativeQuery(queryString.toString());
	q.setParameter(1, newsletterSendingDate);
	q.executeUpdate();
    }
}