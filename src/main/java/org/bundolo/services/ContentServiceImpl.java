package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.Constants;
import org.bundolo.dao.ContentDAO;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.PageKindType;
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
	return contentDAO.findByTitle(title, ContentKindType.forum_topic);
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
			contentDB.setLastActivity(new Date());
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
    public Content findEpisode(String serialTitle, String title) {
	return contentDAO.findEpisode(serialTitle, title);
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
}
