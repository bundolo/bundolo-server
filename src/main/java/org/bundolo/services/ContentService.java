package org.bundolo.services;

import java.util.List;
import java.util.Map;

import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.PageKindType;

public interface ContentService {

    public Content findContent(Long contentId);

    // public void saveContent(Long contentId, Long authorUserId, Long
    // parentPageId, Long parentContentId, ContentKindType kind, String text,
    // String locale, Date creationDate, ContentStatusType contentStatus) throws
    // Exception;
    // public void updateContent(Long contentId, Long authorUserId, Long
    // parentPageId, Long parentContentId, ContentKindType kind, String text,
    // String locale, Date creationDate, ContentStatusType contentStatus) throws
    // Exception;
    // public void saveOrUpdateContent(Long contentId, Long authorUserId, Long
    // parentPageId, Long parentContentId, ContentKindType kind, String text,
    // String locale, Date creationDate, ContentStatusType contentStatus) throws
    // Exception;
    public Long saveContent(Content content) throws Exception;

    public void updateContent(Content content) throws Exception;

    // public void saveOrUpdateContent(ContentDTO contentDTO) throws Exception;
    public void deleteContent(Long contentId) throws Exception;

    /**
     * Get content comments.
     * 
     * @param parentContentId
     * @param kind
     * @return
     * @throws Exception
     */
    public List<Content> findContents(Long parentContentId, ContentKindType kind) throws Exception;

    public Map<String, String> getLabelsForLocale(String locale) throws Exception;

    public List<Content> findItemListContents(String query, Integer start, Integer end) throws Exception;

    public Integer findItemListContentsCount(String query) throws Exception;

    public Content getDescriptionContent(Long parentContentId, ContentKindType kind) throws Exception;

    public void saveLabels(Map<String, String> labels) throws Exception;

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
}