package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.UserProfile;
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

}