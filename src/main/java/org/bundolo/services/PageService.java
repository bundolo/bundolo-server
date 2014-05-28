package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Page;

public interface PageService {

    public Page findPage(Long pageId);

    // public void savePage(Long pageId, Long authorUserId, Long parentPageId,
    // Integer displayOrder, PageStatusType pageStatus, PageKindType kind,
    // String name, Date creationDate) throws Exception;
    // public void updatePage(Long pageId, Long authorUserId, Long parentPageId,
    // Integer displayOrder, PageStatusType pageStatus, PageKindType kind,
    // String name, Date creationDate) throws Exception;
    // public void saveOrUpdatePage(Long pageId, Long authorUserId, Long
    // parentPageId, Integer displayOrder, PageStatusType pageStatus,
    // PageKindType kind, String name, Date creationDate) throws Exception;
    public void savePage(Page page) throws Exception;

    public void updatePage(Page page) throws Exception;

    public void updatePages(List<Page> pages) throws Exception;

    // public void saveOrUpdatePage(PageDTO pageDTO) throws Exception;
    public void deletePage(Long pageId) throws Exception;

    // public List<PageDTO> findAllPages() throws Exception; //TODO probably not
    // needed
    public List<Page> findChildPages(Long parentPageId) throws Exception;

    public Integer findPagesCount(Long parentPageId) throws Exception;

    // public PageDTO findNextPage(Long previousPageId) throws Exception; //TODO
    // probably not needed
    // public PageDTO findHomePage() throws Exception;
    public List<Object> findNavigationPages() throws Exception;

    public String getAsciiArt(String text) throws Exception;
}
