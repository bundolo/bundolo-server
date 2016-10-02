package org.bundolo.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.Constants;
import org.bundolo.DateUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.ContentDAO;
import org.bundolo.dao.RatingDAO;
import org.bundolo.dao.UserDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.User;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ratingService")
public class RatingServiceImpl implements RatingService {

	private static final Logger logger = Logger.getLogger(RatingServiceImpl.class.getName());

	@Autowired
	private RatingDAO ratingDAO;

	@Autowired
	private ContentDAO contentDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private DateUtils dateUtils;

	@PostConstruct
	public void init() throws Exception {
	}

	@PreDestroy
	public void destroy() {
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Rating findPersonalRating(Long contentId) {
		String senderUsername = SecurityUtils.getUsername();
		if (senderUsername != null) {
			Rating generalRating = ratingDAO.findRating(contentId, null);
			if (generalRating != null) {
				Rating result = ratingDAO.findRating(contentId, senderUsername);
				if (result == null) {
					Content content = contentDAO.findById(contentId);
					if (content != null) {
						result = new Rating(null, senderUsername, RatingKindType.personal, dateUtils.newDate(),
								RatingStatusType.active, Constants.DEFAULT_PERSONAL_RATING, generalRating.getValue(),
								content);
						ratingDAO.persist(result);
					}
				} else {
					result.setLastActivity(dateUtils.newDate());
					result.setHistorical(generalRating.getValue());
					ratingDAO.merge(result);
				}
				return result;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ReturnMessageType updateRating(Rating rating) {
		logger.log(Level.INFO, "updateRating rating: " + rating);
		try {
			if (rating == null || rating.getRatingId() == null || rating.getValue() == null) {
				return ReturnMessageType.no_data;
			}
			if ((rating.getValue() > Constants.MAX_PERSONAL_RATING)
					|| (rating.getValue() < Constants.MIN_PERSONAL_RATING)) {
				return ReturnMessageType.wrong_value;
			}
			String senderUsername = SecurityUtils.getUsername();
			if (senderUsername == null) {
				return ReturnMessageType.anonymous_not_allowed;
			}
			Rating ratingDB = ratingDAO.findById(rating.getRatingId());
			if (ratingDB == null) {
				return ReturnMessageType.not_found;
			}
			ratingDB.setLastActivity(dateUtils.newDate());
			ratingDB.setValue(rating.getValue());
			ratingDAO.merge(ratingDB);
			return ReturnMessageType.success;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "updateRating exception: " + ex);
			return ReturnMessageType.exception;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseEntity<String> resetHistoricalRatings(String slug) {
		logger.log(Level.INFO, "resetHistoricalRatings slug: " + slug);
		try {
			if (slug == null) {
				return new ResponseEntity<String>(ReturnMessageType.no_data.name(), HttpStatus.BAD_REQUEST);
			}
			String senderUsername = SecurityUtils.getUsername();
			if (senderUsername != null) {
				User user = userDAO.findById(senderUsername);
				if (user.getDescriptionContent().getSlug().equals(slug)) {
					// find ratings and update
					ratingDAO.resetHistoricalRatings(senderUsername);
					return new ResponseEntity<String>(slug, HttpStatus.OK);
				} else {
					// user is not the owner
					return new ResponseEntity<String>(ReturnMessageType.not_owner.name(), HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<String>(ReturnMessageType.anonymous_not_allowed.name(),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "resetHistoricalRatings exception: " + ex);
			return new ResponseEntity<String>(ReturnMessageType.exception.name(), HttpStatus.BAD_REQUEST);
		}
	}
}
