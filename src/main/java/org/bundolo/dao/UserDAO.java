package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public class UserDAO extends JpaDAO<String, User> {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    @SuppressWarnings("unchecked")
    public List<User> findUsers(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	// Session session = entityManager.unwrap(Session.class);
	// session.enableFilter("descriptionFilter").setParameter("kind", "user_description");
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT u FROM User u WHERE user_profile_status='active'");
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
    public User findNext(String username, String orderBy, String fixBy, boolean ascending) {
	// TODO this is not nice. since we don't keep user status in User, we retrieve UserProfile and then get User
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT u1 FROM UserProfile u1, UserProfile u2");
	queryString.append(" WHERE u2.username = '" + username + "'");
	queryString.append(" AND u1.userProfileStatus='active'");
	if (StringUtils.isNotBlank(fixBy)) {
	    queryString.append(" AND u1." + fixBy + "=u2." + fixBy);
	}
	queryString.append(" AND u1." + orderBy + (ascending ? ">" : "<") + "u2." + orderBy);
	queryString.append(" ORDER BY u1." + orderBy + " " + (ascending ? "ASC" : "DESC"));
	logger.log(Level.WARNING, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	q.setMaxResults(1);
	List<UserProfile> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    return findById(resultList.get(0).getUsername());
	} else {
	    return null;
	}
    }

}