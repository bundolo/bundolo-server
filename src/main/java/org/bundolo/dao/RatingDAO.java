package org.bundolo.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.springframework.stereotype.Repository;

@Repository("ratingDAO")
public class RatingDAO extends JpaDAO<Long, Rating> {

    private static final Logger logger = Logger.getLogger(RatingDAO.class.getName());

    @SuppressWarnings("unchecked")
    public Rating findRating(Long contentId, String authorUsername) {
	if (contentId == null) {
	    return null;
	}
	StringBuilder queryString = new StringBuilder();
	queryString.append("SELECT r FROM Rating r");
	queryString.append(" WHERE parent_content_id =?1");
	if (StringUtils.isNotBlank(authorUsername)) {
	    queryString.append(" AND r.kind = '" + RatingKindType.personal + "'");
	    queryString.append(" AND author_username =?2");
	} else {
	    queryString.append(" AND r.kind = '" + RatingKindType.general + "'");
	    queryString.append(" AND author_username is null");
	}
	queryString.append(" AND rating_status='" + RatingStatusType.active + "'");
	logger.log(Level.INFO, "queryString: " + queryString.toString());
	Query q = entityManager.createQuery(queryString.toString());
	q.setParameter(1, contentId);
	if (StringUtils.isNotBlank(authorUsername)) {
	    q.setParameter(2, authorUsername);
	}
	q.setMaxResults(1);
	List<Rating> resultList = q.getResultList();
	if (resultList != null && resultList.size() > 0) {
	    Rating result = resultList.get(0);
	    return result;
	} else {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    public void resetHistoricalRatings(String authorUsername) {
	StringBuilder resetHistoricalRatingsQueryString = new StringBuilder();
	resetHistoricalRatingsQueryString.append("update rating r set historical=general_rating.value from");
	resetHistoricalRatingsQueryString
		.append(" (select parent_content_id, value from rating r1 where r1.author_username is null and r1.kind='general') as general_rating");
	resetHistoricalRatingsQueryString
		.append(" where r.author_username=?1 and r.kind='personal' and r.rating_status='active'");
	resetHistoricalRatingsQueryString.append(" and r.parent_content_id=general_rating.parent_content_id;");
	logger.log(Level.INFO, "resetHistoricalRatingsQueryString: " + resetHistoricalRatingsQueryString.toString());
	Query ratingsQuery = entityManager
		.createNativeQuery(resetHistoricalRatingsQueryString.toString(), Rating.class);
	ratingsQuery.setParameter(1, authorUsername);
	ratingsQuery.executeUpdate();
    }

}