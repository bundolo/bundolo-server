package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.dao.ContestDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Contest;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.ContestKindType;
import org.bundolo.model.enumeration.ContestStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("contestService")
public class ContestServiceImpl implements ContestService {

    private static final Logger logger = Logger.getLogger(ContestServiceImpl.class.getName());

    @Autowired
    private ContestDAO contestDAO;

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
    private Boolean saveContest(Contest contest) {
	try {
	    contest.setContestStatus(ContestStatusType.active);
	    contest.setCreationDate(new Date());
	    contest.setKind(ContestKindType.general);
	    Content descriptionContent = contest.getDescriptionContent();
	    descriptionContent.setAuthorUsername(contest.getAuthorUsername());
	    descriptionContent.setContentStatus(ContentStatusType.active);
	    descriptionContent.setCreationDate(contest.getCreationDate());
	    descriptionContent.setKind(ContentKindType.contest_description);
	    descriptionContent.setLocale(Constants.DEFAULT_LOCALE);
	    contestDAO.persist(contest);
	    return true;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveContest exception: " + ex);
	}
	return false;
    }

    // @Override
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // public void deleteContest(Long contestId) throws Exception {
    // Contest contest = contestDAO.findById(contestId);
    // if (contest != null) {
    // contestDAO.remove(contest);
    // }
    // }

    @Override
    public List<Contest> findContests(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	return contestDAO.findContests(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    public Contest findContest(String title) {
	return contestDAO.findByTitle(title);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean saveOrUpdateContest(Contest contest) {
	try {
	    if (contest == null || contest.getDescriptionContent() == null
		    || StringUtils.isBlank(contest.getDescriptionContent().getName())) {
		return false;
	    }
	    UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
		    .getContext().getAuthentication();
	    if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
		if (contest.getContestId() == null) {
		    contest.setAuthorUsername((String) authentication.getPrincipal());
		    return saveContest(contest);
		} else {
		    Contest contestDB = contestDAO.findById(contest.getContestId());
		    if (contestDB == null) {
			// no such contest
			return false;
		    } else {
			if (!((String) authentication.getPrincipal()).equals(contestDB.getAuthorUsername())) {
			    // user is not the owner
			    return false;
			}
			Content descriptionContent = contest.getDescriptionContent();
			Content descriptionContentDB = contestDB.getDescriptionContent();
			descriptionContentDB.setName(descriptionContent.getName());
			descriptionContentDB.setText(descriptionContent.getText());
			contestDB.setExpirationDate(contest.getExpirationDate());
			contestDAO.merge(contestDB);
			return true;
		    }
		}
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveOrUpdateContest exception: " + ex);
	}
	return false;
    }

    @Override
    public void clearSession() {
	contestDAO.clear();
    }

}
