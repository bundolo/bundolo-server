package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.springframework.stereotype.Repository;

@Repository("ratingDAO")
public class RatingDAO extends JpaDAO<Long, Rating> {

    private static final Logger logger = Logger.getLogger(RatingDAO.class.getName());

    @SuppressWarnings("unchecked")
    public Rating findPersonalRating(Long contentId, String authorUsername) {
	String queryString = "SELECT r FROM Rating r";
	queryString += " WHERE r.kind = '" + RatingKindType.personal + "'";
	queryString += " AND parent_content_id =?1";
	queryString += " AND author_username =?2";
	queryString += " AND rating_status='" + RatingStatusType.active + "'";
	logger.log(Level.INFO, "queryString: " + queryString);

	Query q = entityManager.createQuery(queryString);
	q.setParameter(1, contentId);
	q.setParameter(2, authorUsername);
	q.setMaxResults(1);
	List<Rating> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    Rating result = resultList.get(0);
	    return result;
	} else {
	    return null;
	}
    }

}