package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.SessionUtils;
import org.bundolo.dao.ContentDAO;
import org.bundolo.dao.ContestDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Contest;
import org.bundolo.model.enumeration.ContentKindType;
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
    private ContentDAO contentDAO;

    @Autowired
    private ContentService contentService;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long saveContest(Contest contest) throws Exception {
	Long result = null;
	Contest contestDB = null;
	if (contest.getContestId() != null) {
	    contestDB = contestDAO.findById(contest.getContestId());
	}
	if (contestDB == null) {
	    if (contest.getDescriptionContent() != null) {
		Long contentId = contentService.saveContent(contest.getDescriptionContent());
		contestDB = new Contest(contest.getContestId(), SessionUtils.getUsername(), contentId,
			contest.getKind(), new Date(), contest.getExpirationDate(), contest.getContestStatus());
		try {
		    contestDAO.persist(contestDB);
		} catch (Exception ex) {
		    contentService.deleteContent(contentId);
		    throw new Exception("db exception");
		}
		result = contestDB.getContestId();
	    }
	}
	return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateContest(Contest contest) throws Exception {
	Contest contestDB = contestDAO.findById(contest.getContestId());

	if (contestDB != null) {
	    Content descriptionContent = contest.getDescriptionContent();
	    if (descriptionContent != null) {
		if (descriptionContent.getContentId() == null) {
		    contentService.saveContent(contest.getDescriptionContent());
		} else {
		    contentService.updateContent(contest.getDescriptionContent());
		}
	    }
	    contestDB.setContestStatus(contest.getContestStatus());
	    contestDB.setCreationDate(contest.getCreationDate());
	    contestDB.setExpirationDate(contest.getExpirationDate());
	    contestDB.setKind(contest.getKind());
	    try {
		contestDAO.merge(contestDB);
	    } catch (Exception ex) {
		throw new Exception("db exception");
	    }
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteContest(Long contestId) throws Exception {
	Contest contest = contestDAO.findById(contestId);
	if (contest != null) {
	    contestDAO.remove(contest);
	}
    }

    @Override
    public List<Contest> findItemListContests(String query, Integer start, Integer end) throws Exception {
	List<Contest> contests = contestDAO.findItemListContests(query, start, end);
	if (contests != null) {
	    for (Contest contest : contests) {
		Content descriptionContent = contentDAO.findContentForLocale(contest.getDescriptionContentId(),
			ContentKindType.contest_description, SessionUtils.getUserLocale());
		if (descriptionContent != null) {
		    contest.setDescriptionContent(descriptionContent);
		}
	    }
	}
	return contests;
    }

    @Override
    public Integer findItemListContestsCount(String query) throws Exception {
	return contestDAO.findItemListContestsCount(query);
    }

}
