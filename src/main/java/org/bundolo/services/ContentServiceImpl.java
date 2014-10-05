package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.CommentDAO;
import org.bundolo.dao.ContentDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.PageKindType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.bundolo.model.enumeration.TextColumnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("contentService")
public class ContentServiceImpl implements ContentService {

    private static final Logger logger = Logger.getLogger(ContentServiceImpl.class.getName());

    @Autowired
    private ContentDAO contentDAO;

    @Autowired
    private CommentDAO commentDAO;

    @PostConstruct
    public void init() throws Exception {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    public Content findContent(Long contentId) {
	Content content = contentDAO.findById(contentId);
	return content;
    }

    @Override
    public List<Content> findTexts(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	return contentDAO.findTexts(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    public List<Content> findAnnouncements(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	return contentDAO.findAnnouncements(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    public List<Content> findSerials(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	return contentDAO.findSerials(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    public List<Content> findTopics(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	return contentDAO.findTopics(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Content getPageDescriptionContent(PageKindType pageKind) {
	Content pageDescriptionContent = contentDAO.getPageDescriptionContent(pageKind);
	if (pageDescriptionContent != null) {
	    Rating rating = pageDescriptionContent.getRating();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, new Date(), RatingStatusType.active,
			Constants.DEFAULT_RATING_INCREMENT, pageDescriptionContent);
		pageDescriptionContent.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + Constants.DEFAULT_RATING_INCREMENT);
		rating.setLastActivity(new Date());
	    }
	    contentDAO.merge(pageDescriptionContent);
	}
	return pageDescriptionContent;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Content findAnnouncement(String title) {
	Content announcement = contentDAO.findByTitle(title, ContentKindType.news);
	if (announcement != null) {
	    Rating rating = announcement.getRating();
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = announcement.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !announcement.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? new Date()
		    : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, announcement);
		announcement.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    contentDAO.merge(announcement);
	}
	return announcement;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Content findSerial(String title) {
	Content serial = contentDAO.findByTitle(title, ContentKindType.episode_group);
	if (serial != null) {
	    Rating rating = serial.getRating();
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = serial.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !serial.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? new Date()
		    : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, serial);
		serial.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    contentDAO.merge(serial);
	}
	return serial;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Content findText(String username, String title) {
	Content text = contentDAO.findText(username, title);
	if (text != null) {
	    Rating rating = text.getRating();
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = text.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !text.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? new Date()
		    : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, text);
		text.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    contentDAO.merge(text);
	}
	return text;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Content findTopic(String title) {
	Content topic = contentDAO.findByTitle(title, ContentKindType.forum_topic);
	if (topic != null) {
	    Rating rating = topic.getRating();
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = topic.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !topic.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? new Date()
		    : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, topic);
		topic.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    contentDAO.merge(topic);
	}
	return topic;
    }

    @Override
    public List<Content> findConnectionGroups() {
	return contentDAO.findConnectionGroups();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Boolean saveContent(Content content) {
	try {
	    if (contentViolatesDBConstraints(content)) {
		return false;
	    }
	    if (ContentKindType.episode.equals(content.getKind())) {
		// if this is episode and the last one in the serial is pending, saving is not allowed
		List<Content> episodes = contentDAO.findEpisodes(content.getParentContent().getContentId(), 0, -1);
		if (episodes != null && episodes.size() > 0
			&& ContentStatusType.pending.equals(episodes.get(episodes.size() - 1).getContentStatus())) {
		    return false;
		}
	    }
	    if (content.getContentStatus() == null) {
		content.setContentStatus(ContentStatusType.active);
	    }
	    Date creationDate = new Date();
	    content.setCreationDate(creationDate);
	    content.setLastActivity(creationDate);
	    content.setLocale(Constants.DEFAULT_LOCALE);
	    if (ContentKindType.text.equals(content.getKind())) {
		Content descriptionContent = (Content) content.getDescription().toArray()[0];
		descriptionContent.setAuthorUsername(content.getAuthorUsername());
		descriptionContent.setContentStatus(ContentStatusType.active);
		descriptionContent.setCreationDate(content.getCreationDate());
		descriptionContent.setLastActivity(creationDate);
		descriptionContent.setKind(ContentKindType.text_description);
		descriptionContent.setLocale(Constants.DEFAULT_LOCALE);
	    }
	    contentDAO.persist(content);
	    return true;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveContent exception: " + ex);
	}
	return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean saveOrUpdateContent(Content content, boolean anonymousAllowed) {
	try {
	    if (content == null) {
		return false;
	    }
	    if (ContentKindType.episode.equals(content.getKind()) && content.getParentContent().getContentId() == null) {
		// episode that is not attached to a serial is not allowed
		return false;
	    }

	    String senderUsername = SecurityUtils.getUsername();
	    if (senderUsername != null || anonymousAllowed) {
		if (content.getContentId() == null) {
		    content.setAuthorUsername(senderUsername);
		    return saveContent(content);
		} else {
		    Content contentDB = contentDAO.findById(content.getContentId());
		    if (contentDB == null) {
			// no such content
			return false;
		    }
		    if (!senderUsername.equals(contentDB.getAuthorUsername())) {
			// user is not the owner
			return false;
		    }
		    if (!contentDB.getName().equals(content.getName())) {
			content.setAuthorUsername(contentDB.getAuthorUsername());
			if (contentViolatesDBConstraints(content)) {
			    return false;
			}
		    }
		    if (ContentKindType.text.equals(content.getKind())) {
			Content descriptionContent = (Content) content.getDescription().toArray()[0];
			Content descriptionContentDB = (Content) contentDB.getDescription().toArray()[0];
			descriptionContentDB.setText(descriptionContent.getText());
		    }
		    contentDB.setName(content.getName());
		    contentDB.setText(content.getText());
		    if (content.getLastActivity() != null) {
			contentDB.setLastActivity(content.getLastActivity());
		    } else {
			contentDB.setLastActivity(new Date());
		    }
		    if (ContentKindType.episode.equals(content.getKind()) && content.getContentStatus() != null) {
			contentDB.setContentStatus(content.getContentStatus());
		    }
		    contentDAO.merge(contentDB);
		    return true;
		}
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveOrUpdateContent exception: " + ex);
	}
	return false;
    }

    @Override
    public void clearSession() {
	contentDAO.clear();
    }

    @Override
    public List<Content> findTopicGroups() {
	return contentDAO.findTopicGroups();
    }

    @Override
    public List<Content> findPosts(Long parentId, Integer start, Integer end) {
	return contentDAO.findPosts(parentId, start, end);
    }

    @Override
    public List<Content> findEpisodes(Long parentId, Integer start, Integer end) {
	return contentDAO.findEpisodes(parentId, start, end);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Content findEpisode(String serialTitle, String title) {
	Content episode = contentDAO.findEpisode(serialTitle, title);
	if (episode != null) {
	    Rating rating = episode.getRating();
	    // if user that requested this is the author, do not increase rating
	    String senderUsername = SecurityUtils.getUsername();
	    long ratingIncrement = episode.getAuthorUsername().equals(senderUsername) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !episode.getAuthorUsername().equals(senderUsername) || rating == null ? new Date()
		    : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, episode);
		episode.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    contentDAO.merge(episode);
	}
	return episode;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean updateLastActivity(Long contentId, Date lastActivity) {
	logger.log(Level.WARNING, "updateLastActivity: " + contentId);
	Content contentDB = contentDAO.findById(contentId);
	if (contentDB == null) {
	    // no such content
	    return false;
	} else {
	    contentDB.setLastActivity(lastActivity);
	    contentDAO.merge(contentDB);
	    return true;
	}
    }

    @Override
    public List<Content> findStatistics(String username) {
	return contentDAO.findStatistics(username);
    }

    @Override
    public Content findNext(Long contentId, String orderBy, String fixBy, boolean ascending) {
	return contentDAO.findNext(contentId, orderBy, fixBy, ascending);
    }

    @Override
    public Long deleteAnnouncement(String title) {
	// TODO
	return null;
    }

    @Override
    public Long deleteSerial(String title) {
	// TODO
	return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long deleteEpisode(String serialTitle, String title) {
	logger.log(Level.WARNING, "deleteEpisode: username: " + serialTitle + ", title: " + title);
	Content episode = contentDAO.findEpisode(serialTitle, title);
	if (episode == null) {
	    // no such content
	    return null;
	} else {
	    if (!episode.getAuthorUsername().equals(SecurityUtils.getUsername())) {
		// user is not the owner
		return null;
	    }
	    // TODO it might be sufficient just to check episode status
	    Content nextEpisode = contentDAO.findNext(episode.getContentId(), TextColumnType.valueOf("date")
		    .getColumnName(), null, true);
	    if (nextEpisode != null) {
		// there is subsequent episode
		return null;
	    }
	    episode.setContentStatus(ContentStatusType.disabled);

	    // TODO disable all children
	    contentDAO.merge(episode);
	    return episode.getContentId();
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long deleteText(String username, String title) {
	logger.log(Level.WARNING, "deleteText: username: " + username + ", title: " + title);
	Content text = contentDAO.findText(username, title);
	if (text == null) {
	    // no such content
	    return null;
	} else {
	    if (!text.getAuthorUsername().equals(SecurityUtils.getUsername())) {
		// user is not the owner
		return null;
	    }
	    text.setContentStatus(ContentStatusType.disabled);
	    contentDAO.merge(text);
	    return text.getContentId();
	}
    }

    @Override
    public Long deleteTopic(String title) {
	// TODO
	return null;
    }

    private boolean contentViolatesDBConstraints(Content content) {
	switch (content.getKind()) {
	case news:
	    return contentDAO.findByTitle(content.getName(), ContentKindType.news) != null;
	case forum_topic:
	    return contentDAO.findByTitle(content.getName(), ContentKindType.forum_topic) != null;
	case text:
	    return contentDAO.findText(content.getAuthorUsername(), content.getName()) != null;
	case episode_group:
	    return contentDAO.findByTitle(content.getName(), ContentKindType.episode_group) != null;
	case episode:
	    return contentDAO.findEpisode(content.getParentContent().getName(), content.getName()) != null;
	default:
	    return true;
	}
    }
}