package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.Constants;
import org.bundolo.dao.ContentDAO;
import org.bundolo.dao.RatingDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.PageKindType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("contentService")
public class ContentServiceImpl implements ContentService {

    private static final Logger logger = Logger.getLogger(ContentServiceImpl.class.getName());

    @Autowired
    private ContentDAO contentDAO;

    @Autowired
    private RatingDAO ratingDAO;

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

    // @Override
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // public void deleteContent(Long contentId) throws Exception {
    // Content content = contentDAO.findById(contentId);
    // if (content != null) {
    // contentDAO.remove(content);
    // }
    // }

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
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, new Date(), RatingStatusType.active,
			Constants.DEFAULT_RATING_INCREMENT, announcement);
		announcement.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + Constants.DEFAULT_RATING_INCREMENT);
		rating.setLastActivity(new Date());
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
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, new Date(), RatingStatusType.active,
			Constants.DEFAULT_RATING_INCREMENT, serial);
		serial.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + Constants.DEFAULT_RATING_INCREMENT);
		rating.setLastActivity(new Date());
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
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, new Date(), RatingStatusType.active,
			Constants.DEFAULT_RATING_INCREMENT, text);
		text.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + Constants.DEFAULT_RATING_INCREMENT);
		rating.setLastActivity(new Date());
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
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, new Date(), RatingStatusType.active,
			Constants.DEFAULT_RATING_INCREMENT, topic);
		topic.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + Constants.DEFAULT_RATING_INCREMENT);
		rating.setLastActivity(new Date());
	    }
	    contentDAO.merge(topic);
	}
	return topic;
    }

    @Override
    public List<Content> findConnectionGroups() {
	// TODO Auto-generated method stub
	return contentDAO.findConnectionGroups();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Boolean saveContent(Content content) {
	try {
	    content.setContentStatus(ContentStatusType.active);
	    Date creationDate = new Date();
	    content.setCreationDate(creationDate);
	    content.setLastActivity(creationDate);
	    content.setLocale(Constants.DEFAULT_LOCALE);
	    if (ContentKindType.text.equals(content.getKind())) {
		Content descriptionContent = (Content) content.getDescription().toArray()[0];
		descriptionContent.setAuthorUsername(content.getAuthorUsername());
		descriptionContent.setContentStatus(ContentStatusType.active);
		descriptionContent.setCreationDate(content.getCreationDate());
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
	    UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
		    .getContext().getAuthentication();

	    logger.log(Level.WARNING, "saveOrUpdateContent authentication: " + authentication);
	    if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")) || anonymousAllowed) {
		if (content.getContentId() == null) {
		    if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
			content.setAuthorUsername(null);
		    } else {
			content.setAuthorUsername((String) authentication.getPrincipal());
		    }
		    return saveContent(content);
		} else {
		    Content contentDB = contentDAO.findById(content.getContentId());
		    if (contentDB == null) {
			// no such content
			return false;
		    } else {
			if (!((String) authentication.getPrincipal()).equals(contentDB.getAuthorUsername())) {
			    // user is not the owner
			    return false;
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
			contentDAO.merge(contentDB);
			return true;
		    }
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
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, new Date(), RatingStatusType.active,
			Constants.DEFAULT_RATING_INCREMENT, episode);
		episode.setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + Constants.DEFAULT_RATING_INCREMENT);
		rating.setLastActivity(new Date());
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
}