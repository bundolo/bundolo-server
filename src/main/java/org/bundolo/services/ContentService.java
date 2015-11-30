package org.bundolo.services;

import java.util.Date;
import java.util.List;

import org.bundolo.model.Content;
import org.bundolo.model.enumeration.PageKindType;
import org.springframework.http.ResponseEntity;

public interface ContentService {

    public Content findContent(Long contentId);

    // this method is only for back end, rating should not be updated
    public Content findContent(String slug);

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

    public Content findAnnouncement(String slug);

    public Content findSerial(String slug);

    public List<Content> findEpisodes(Long parentId, Integer start, Integer end);

    public Content findEpisode(String slug);

    public Content findText(String slug);

    public Content findTopic(String slug);

    public List<Content> findPosts(Long parentId, Integer start, Integer end);

    public List<Content> findAuthorItems(String slug, Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    // public Boolean saveOrUpdateContent(Content content);

    public ResponseEntity<String> saveOrUpdateContent(Content content, boolean anonymousAllowed);

    public Boolean updateLastActivity(Long contentId, Date lastActivity);

    public Content findNext(Long contentId, String orderBy, String fixBy, boolean ascending);

    public void clearSession();

    public Long deleteAnnouncement(String slug);

    public Long deleteSerial(String slug);

    public Long deleteEpisode(String slug);

    public Long deleteText(String slug);

    public Long deleteTopic(String slug);

    public List<Content> findRecent(Date fromDate, Integer limit);

    public List<Content> findItemListItems(String itemListIds);

    // TODO remove after slugs are populated
    public int populateSlugs(int page);

}