package org.bundolo.services;

import java.util.Date;
import java.util.List;

import org.bundolo.model.Content;
import org.bundolo.model.enumeration.PageKindType;
import org.springframework.http.ResponseEntity;

public interface ContentService {

    public Content findContent(Long contentId);

    public List<Content> findTexts(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public List<Content> findAnnouncements(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    public List<Content> findSerials(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public List<Content> findTopics(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public Content getPageDescriptionContent(PageKindType pageKind); // to simplify things, pages are out of scope in
								     // the first release

    public List<Content> findConnectionGroups();

    public List<Content> findTopicGroups();

    public Content findAnnouncement(String title);

    public Content findSerial(String title);

    public List<Content> findEpisodes(Long parentId, Integer start, Integer end);

    public Content findEpisode(String serialTitle, String title);

    public Content findText(String username, String title);

    public Content findTopic(String title);

    public List<Content> findPosts(Long parentId, Integer start, Integer end);

    public List<Content> findAuthorItems(String username, Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    // public Boolean saveOrUpdateContent(Content content);

    public ResponseEntity<String> saveOrUpdateContent(Content content, boolean anonymousAllowed);

    public Boolean updateLastActivity(Long contentId, Date lastActivity);

    public Content findNext(Long contentId, String orderBy, String fixBy, boolean ascending);

    public void clearSession();

    public Long deleteAnnouncement(String title);

    public Long deleteSerial(String title);

    public Long deleteEpisode(String serialTitle, String title);

    public Long deleteText(String username, String title);

    public Long deleteTopic(String title);

    public List<Content> findRecent(Date fromDate, Integer limit);

    public List<Content> findItemListItems(String itemListIds);

}