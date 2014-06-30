package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.SessionUtils;
import org.bundolo.dao.ContentDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.PageKindType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("contentService")
public class ContentServiceImpl implements ContentService, ApplicationContextAware {

    private static final Logger logger = Logger.getLogger(ContentServiceImpl.class.getName());

    private ApplicationContext applicationContext;

    @Autowired
    private ContentDAO contentDAO;

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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteContent(Long contentId) throws Exception {
	Content content = contentDAO.findById(contentId);
	if (content != null) {
	    contentDAO.remove(content);
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long saveContent(Content content) throws Exception {
	// Map<String, String> labels =
	// getLabelsForLocale(SessionUtils.getUserLocale());

	// ServerValidation.exception(LabelType.text_adding_failed.name(),
	// LabelType.text_name.name());
	// new ServerValidation(true).notEqual(contentDTO.getName(), "kiloster",
	// LabelType.text_name.name());

	Long result = null;
	Content contentDB = null;
	if (content.getContentId() != null) {
	    contentDB = contentDAO.findById(content.getContentId());
	}
	if (contentDB == null) {
	    Rating rating;
	    if (content.getRating() != null) {
		rating = content.getRating();
	    } else {
		rating = new Rating(null, SessionUtils.getUsername(), RatingKindType.general, new Date(),
			RatingStatusType.active, 0L);
	    }
	    contentDB = new Content(content.getContentId(), SessionUtils.getUsername(), content.getKind(),
		    content.getName(), content.getText(), SessionUtils.getUserLocale(), new Date(),
		    content.getContentStatus(), rating);
	    rating.setParentContent(contentDB);
	    try {
		contentDAO.persist(contentDB);
	    } catch (Exception ex) {
		// TODO once db constraints fully implemented, check type of
		// exception here and throw appropriate validation exception
		throw new Exception("db exception");
	    }
	    /*
	    	    ContentKindType descriptionContentKind = Utils.getDescriptionContentKind(content.getKind());
	    	    if (descriptionContentKind != null) {
	    		Content descriptionContent = new Content(null, SessionUtils.getUsername(), contentDB.getContentId(),
	    			descriptionContentKind, null, null, SessionUtils.getUserLocale(), new Date(),
	    			ContentStatusType.active, null);
	    		if (content.getDescriptionContent() != null) {
	    		    descriptionContent.setText(content.getDescriptionContent().getText());
	    		}
	    		contentDAO.persist(descriptionContent);
	    	    }*/
	    result = contentDB.getContentId();
	}
	return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateContent(Content content) throws Exception {
	logger.log(Level.FINE, "updateContent: " + content.getContentId());
	Content contentDB = contentDAO.findById(content.getContentId());

	if (contentDB != null) {
	    // content.setAuthorUsername(getUsername()); //we want to keep the
	    // original author
	    contentDB.setKind(content.getKind());
	    contentDB.setName(content.getName());
	    contentDB.setText(content.getText());
	    // content.setLocale(getUserLocale());
	    // content.setCreationDate(contentDTO.getCreationDate());
	    contentDB.setContentStatus(content.getContentStatus());
	    if (contentDB.getRating() == null) {
		Rating rating = new Rating(null, SessionUtils.getUsername(), RatingKindType.general, new Date(),
			RatingStatusType.active, 0L);
		contentDB.setRating(rating);
		rating.setParentContent(contentDB);
	    }
	    if (content.getRating() != null) {
		contentDB.getRating().setValue(content.getRating().getValue());
	    }
	    try {
		contentDAO.merge(contentDB);
	    } catch (Exception ex) {
		// TODO once db constraints fully implemented, check type of
		// exception here and throw appropriate validation exception
		throw new Exception("db exception");
	    }

	    // TODO
	    /*	    ContentKindType descriptionContentKind = Utils.getDescriptionContentKind(content.getKind());
	    	    if (content.getDescriptionContent() != null && descriptionContentKind != null) {
	    		Content descriptionContent = null;
	    		if (content.getContentId() != null) {
	    		    // descriptionContent = getDescriptionContent(content.getContentId(), content.getKind());
	    		}
	    		if (descriptionContent != null) {
	    		    descriptionContent.setText(content.getDescriptionContent().getText());
	    		    updateContent(descriptionContent);
	    		} else { // just for db inconsistencies
	    		    Content newDescriptionContent = new Content(null, SessionUtils.getUsername(),
	    			    contentDB.getContentId(), descriptionContentKind, null, content.getDescriptionContent()
	    				    .getText(), SessionUtils.getUserLocale(), new Date(), ContentStatusType.active,
	    			    null);
	    		    contentDAO.persist(newDescriptionContent);
	    		}
	    	    }*/
	}

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	this.applicationContext = applicationContext;
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
    public Content getPageDescriptionContent(PageKindType pageKind) {
	return contentDAO.getPageDescriptionContent(pageKind);
    }

    @Override
    public Content findAnnouncement(String title) {
	return contentDAO.findByTitle(title, ContentKindType.news);
    }

    @Override
    public Content findSerial(String title) {
	return contentDAO.findByTitle(title, ContentKindType.episode_group);
    }

    @Override
    public Content findText(String username, String title) {
	return contentDAO.findText(username, title);
    }

    @Override
    public Content findTopic(String title) {
	return contentDAO.findByText(title, ContentKindType.forum_topic);
    }
}
