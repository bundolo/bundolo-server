package org.bundolo.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.DateUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.ContestDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Contest;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.ContestKindType;
import org.bundolo.model.enumeration.ContestStatusType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("contestService")
public class ContestServiceImpl implements ContestService {

    private static final Logger logger = Logger.getLogger(ContestServiceImpl.class.getName());

    @Autowired
    private ContestDAO contestDAO;

    @Autowired
    private DateUtils dateUtils;

    @PostConstruct
    public void init() throws Exception {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    public Contest findContest(Long contestId) {
	Contest result = contestDAO.findById(contestId);
	return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private ReturnMessageType saveContest(Contest contest) {
	try {
	    if (contestDAO.findByTitle(contest.getDescriptionContent().getName()) != null) {
		// contest title already taken
		return ReturnMessageType.title_taken;
	    }
	    contest.setContestStatus(ContestStatusType.active);
	    contest.setCreationDate(dateUtils.newDate());
	    contest.setKind(ContestKindType.general);
	    Content descriptionContent = contest.getDescriptionContent();
	    descriptionContent.setAuthorUsername(contest.getAuthorUsername());
	    descriptionContent.setContentStatus(ContentStatusType.active);
	    descriptionContent.setCreationDate(contest.getCreationDate());
	    descriptionContent.setKind(ContentKindType.contest_description);
	    descriptionContent.setLocale(Constants.DEFAULT_LOCALE);
	    descriptionContent.setLastActivity(dateUtils.newDate());
	    contestDAO.persist(contest);
	    return ReturnMessageType.success;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveContest exception: " + ex);
	    return ReturnMessageType.exception;
	}
    }

    @Override
    public List<Contest> findContests(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	return contestDAO.findContests(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Contest findContest(String title) {
	Contest contest = contestDAO.findByTitle(title);
	if (contest != null) {
	    Collection<Rating> ratings = contest.getDescriptionContent().getRating();
	    if (ratings == null) {
		ratings = new ArrayList<Rating>();
		contest.getDescriptionContent().setRating(ratings);
	    }
	    Rating rating = contest.getDescriptionContent().getRating().size() > 0 ? (Rating) contest
		    .getDescriptionContent().getRating().toArray()[0] : null;
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = contest.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !contest.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? dateUtils
		    .newDate() : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, contest.getDescriptionContent());
		contest.getDescriptionContent().getRating().add(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    contestDAO.merge(contest);
	}
	return contest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMessageType saveOrUpdateContest(Contest contest) {
	try {
	    if (contest == null || contest.getDescriptionContent() == null
		    || StringUtils.isBlank(contest.getDescriptionContent().getName())) {
		return ReturnMessageType.no_data;
	    }
	    String senderUsername = SecurityUtils.getUsername();
	    if (senderUsername != null) {
		if (contest.getContestId() == null) {
		    contest.setAuthorUsername(senderUsername);
		    return saveContest(contest);
		} else {
		    Contest contestDB = contestDAO.findById(contest.getContestId());
		    if (contestDB == null) {
			// no such contest
			return ReturnMessageType.not_found;
		    } else {
			if (!contestDB.getAuthorUsername().equals(senderUsername)) {
			    // user is not the owner
			    return ReturnMessageType.not_owner;
			}
			Content descriptionContent = contest.getDescriptionContent();
			Content descriptionContentDB = contestDB.getDescriptionContent();
			if (!descriptionContentDB.getName().equals(descriptionContent.getName())
				&& contestDAO.findByTitle(descriptionContent.getName()) != null) {
			    // new contest title already taken
			    return ReturnMessageType.title_taken;
			}
			descriptionContentDB.setName(descriptionContent.getName());
			descriptionContentDB.setText(descriptionContent.getText());
			descriptionContentDB.setLastActivity(dateUtils.newDate());
			contestDB.setExpirationDate(contest.getExpirationDate());
			contestDAO.merge(contestDB);
			return ReturnMessageType.success;
		    }
		}
	    } else {
		return ReturnMessageType.anonymous_not_allowed;
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveOrUpdateContest exception: " + ex);
	    return ReturnMessageType.exception;
	}
    }

    @Override
    public void clearSession() {
	contestDAO.clear();
    }

    @Override
    public Contest findNext(Long contestId, String orderBy, String fixBy, boolean ascending) {
	return contestDAO.findNext(contestId, orderBy, fixBy, ascending);
    }

    @Override
    public Long deleteContest(String title) {
	// TODO
	return null;
    }

}
