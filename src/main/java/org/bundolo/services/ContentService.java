package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Content;
import org.bundolo.model.enumeration.PageKindType;

public interface ContentService {

    public Content findContent(Long contentId);

    public Long saveContent(Content content) throws Exception;

    public void updateContent(Content content) throws Exception;

    public void deleteContent(Long contentId) throws Exception;

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

    public Content findAnnouncement(String title);

    public Content findSerial(String title);

    public Content findText(String username, String title);

    public Content findTopic(String title);
}