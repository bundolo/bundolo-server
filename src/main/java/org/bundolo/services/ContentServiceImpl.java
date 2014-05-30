package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.GlobalStorage;
import org.bundolo.SessionUtils;
import org.bundolo.Utils;
import org.bundolo.dao.ContentDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.bundolo.services.ContentService#findContents(java.lang.Long,
     * org.bundolo.model.enumeration.ContentKindType)
     */
    @Override
    public List<Content> findContents(Long parentContentId, ContentKindType kind) throws Exception {
	// switch (kind) {
	// case text_comment:
	// contentDTOs.add(getDescriptionContent(parentContentId,
	// ContentKindType.text_description));
	// break;
	// case episode_group_comment:
	// //TODO see about this, retrieving description here instead from list,
	// to improve performance
	// // contentDTOs.add(getDescriptionContent(parentContentId));
	// break;
	// default:
	// break;
	// }

	List<Content> contents = contentDAO.findContents(parentContentId, kind, SessionUtils.getUserLocale());
	if (Utils.hasElements(contents)) {
	    for (Content content : contents) {
		List<Content> childContents = findContents(content.getContentId(), kind);
		if (Utils.hasElements(childContents)) {
		    // TODO 20140527
		    // content.setContentChildren(childContents);
		}
		// contentDTO.setDescriptionContent(getDescriptionContent(content.getContentId(),
		// content.getKind()));
		contents.add(content);
	    }
	}
	return contents;
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
	    contentDB = new Content(content.getContentId(), SessionUtils.getUsername(), content.getParentContentId(),
		    content.getKind(), content.getName(), content.getText(), SessionUtils.getUserLocale(), new Date(),
		    content.getContentStatus(), rating);
	    rating.setParentContent(contentDB);
	    try {
		contentDAO.persist(contentDB);
	    } catch (Exception ex) {
		// TODO once db constraints fully implemented, check type of
		// exception here and throw appropriate validation exception
		throw new Exception("db exception");
	    }

	    ContentKindType descriptionContentKind = Utils.getDescriptionContentKind(content.getKind());
	    if (descriptionContentKind != null) {
		Content descriptionContent = new Content(null, SessionUtils.getUsername(), contentDB.getContentId(),
			descriptionContentKind, null, null, SessionUtils.getUserLocale(), new Date(),
			ContentStatusType.active, null);
		if (content.getDescriptionContent() != null) {
		    descriptionContent.setText(content.getDescriptionContent().getText());
		}
		contentDAO.persist(descriptionContent);
	    }
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
	    contentDB.setParentContentId(content.getParentContentId());
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

	    ContentKindType descriptionContentKind = Utils.getDescriptionContentKind(content.getKind());
	    if (content.getDescriptionContent() != null && descriptionContentKind != null) {
		Content descriptionContent = null;
		if (content.getContentId() != null) {
		    descriptionContent = getDescriptionContent(content.getContentId(), content.getKind());
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
	    }
	}

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, String> getLabelsForLocale(String locale) {
	logger.log(Level.FINE, "getLabelsForLocale: " + locale);
	SessionUtils.setAttribute("locale", locale);
	GlobalStorage globalStorage = (GlobalStorage) applicationContext.getBean("globalStorage");
	return globalStorage.getLabelsForLocale(locale);
    }

    @Override
    public List<Content> findItemListContents(String query, Integer start, Integer end) throws Exception {
	List<Content> contents = contentDAO.findItemListContents(query, start, end);
	return contents;
    }

    @Override
    public Integer findItemListContentsCount(String query) throws Exception {
	return contentDAO.findItemListContentsCount(query);
    }

    @Override
    public Content getDescriptionContent(Long parentContentId, ContentKindType parentKind) {
	logger.log(Level.FINE, "getDescriptionContent: " + parentContentId + ", " + parentKind);
	Content result = null;
	ContentKindType descriptionContentKind = Utils.getDescriptionContentKind(parentKind);
	if (descriptionContentKind != null) {
	    List<Content> descriptionContents = contentDAO.findContents(parentContentId, descriptionContentKind,
		    SessionUtils.getUserLocale());
	    if (Utils.hasElements(descriptionContents)) {
		result = descriptionContents.get(0);
	    }
	}
	return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveLabels(Map<String, String> localeLabels) throws Exception {
	for (String labelName : localeLabels.keySet()) {
	    Content content = contentDAO.findContentForLocale(labelName, ContentKindType.label,
		    SessionUtils.getUserLocale());
	    if (content == null) {
		Content contentDB = new Content(null, SessionUtils.getUsername(), contentDAO.findParentContentId(
			labelName, ContentKindType.label), ContentKindType.label, labelName,
			localeLabels.get(labelName), SessionUtils.getUserLocale(), new Date(),
			ContentStatusType.active, null);
		saveContent(contentDB);
	    } else {
		content.setAuthorUsername(SessionUtils.getUsername());
		content.setText(localeLabels.get(labelName));
		updateContent(content);
	    }
	}
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
}
