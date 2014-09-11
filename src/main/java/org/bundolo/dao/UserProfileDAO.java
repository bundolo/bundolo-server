package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.UserProfileStatusType;
import org.springframework.stereotype.Repository;

@Repository("userProfileDAO")
public class UserProfileDAO extends JpaDAO<Long, UserProfile> {

    private static final Logger logger = Logger.getLogger(UserProfileDAO.class.getName());

    // @Autowired
    // @PersistenceUnit(unitName = "BundoloPostgresPersistenceUnit")
    // EntityManagerFactory entityManagerFactory;

    // @PersistenceContext(unitName = "movie-unit", type =
    // PersistenceContextType.EXTENDED)
    // private EntityManager entityManager;

    // @PostConstruct
    // public void init() {
    // super.setEntityManagerFactory(entityManagerFactory);
    // }

    @SuppressWarnings("unchecked")
    public UserProfile findByField(final String field, final String value) {
	String queryString = "SELECT u FROM " + entityClass.getName() + " u";
	queryString += " WHERE " + field + " " + ((value == null) ? "IS NULL" : "='" + value + "'");
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
    public List<UserProfile> findItemListUsers(final String queryString, final Integer start, final Integer end) {
	// String modifiedQueryString = queryString + " LIMIT " + (end -
	// start + 1) + " OFFSET " + start;
	logger.log(Level.FINE, "queryString: " + queryString);
	Query q = entityManager.createQuery(queryString);
	q.setFirstResult(start);
	q.setMaxResults(end - start + 1);
	return q.getResultList();
    }

    public Integer findItemListUsersCount(final String queryString) {
	String modifiedQueryString = queryString.replace("SELECT u", "SELECT COUNT(u)");
	int orderBySectionStart = modifiedQueryString.indexOf(" ORDER BY ");
	if (orderBySectionStart > -1) {
	    modifiedQueryString = modifiedQueryString.substring(0, orderBySectionStart);
	}
	logger.log(Level.FINE, "queryString: " + modifiedQueryString);

	Query q = entityManager.createQuery(modifiedQueryString);
	return (Integer) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<UserProfile> findUserProfiles(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT u FROM UserProfile u WHERE user_profile_status='active'");
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

    public String findPassword(String username) {
	String result = null;
	UserProfile userProfile = findByField("username", username);
	if (userProfile != null && UserProfileStatusType.active.equals(userProfile.getUserProfileStatus())) {
	    result = userProfile.getPassword();
	}
	return result;
    }

}