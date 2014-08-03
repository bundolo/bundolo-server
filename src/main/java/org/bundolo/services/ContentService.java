package org.bundolo.services;

import java.util.Date;
import java.util.List;

import org.bundolo.model.Content;
import org.bundolo.model.enumeration.PageKindType;

public interface ContentService {

    public Content findContent(Long contentId);

    // public void deleteContent(Long contentId) throws Exception;

    public List<Content> findTexts(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public List<Content> findAnnouncements(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    public List<Content> findSerials(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public List<Content> findTopics(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public Content getPageDescriptionContent(PageKindType pageKind); // to simplify things, pages are out of scope in
								     // first release

    public List<Content> findConnectionGroups();

    public List<Content> findTopicGroups();

    public Content findAnnouncement(String title);

    public Content findSerial(String title);

    public List<Content> findEpisodes(Long parentId, Integer start, Integer end);

    public Content findEpisode(String serialTitle, String title);

    public Content findText(String username, String title);

    public Content findTopic(String title);

    public List<Content> findPosts(Long parentId, Integer start, Integer end);

    // public Boolean saveOrUpdateContent(Content content);

    public Boolean saveOrUpdateContent(Content content, boolean anonymousAllowed);

    public Boolean updateLastActivity(Long contentId, Date lastActivity);

    public void clearSession();
}