package org.bundolo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bundolo.dao.ContentDAO;
import org.bundolo.dao.PageDAO;
import org.bundolo.model.Content;
import org.springframework.beans.factory.annotation.Autowired;

public class GlobalStorage {

    private static final Logger logger = Logger.getLogger(GlobalStorage.class.getName());

    @Autowired
    private ContentDAO contentDAO;

    @Autowired
    private PageDAO pageDAO;

    private Map<String, Map<String, String>> localeLabels;
    private List<Object> navigationPages;

    // TODO design a way to refresh labels and navigation when needed, possibly
    // after they are updated

    public GlobalStorage() {
	logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "constructor");
    }

    public synchronized Map<String, String> getLabelsForLocale(String locale) {
	logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "getLabelsForLocale: " + locale);
	if (localeLabels == null) { // this is the first request so we have to
				    // initialize locales
	    localeLabels = new HashMap<String, Map<String, String>>(); // get
								       // all
								       // locales
								       // and
								       // set
								       // labels
								       // for
								       // them
	    List<String> locales = contentDAO.getLocales();
	    if ((locales != null) && (locales.size() > 0)) {
		for (String tempLocale : locales) {
		    Map<String, String> thisLocaleLabels = new HashMap<String, String>();
		    logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "tempLocale: " + tempLocale);
		    List<Content> contents = contentDAO.getLabelsForLocale(tempLocale);
		    for (Content content : contents) {
			if (content.getParentContentId() == null) {
			    thisLocaleLabels.put(content.getName(), content.getText());
			} else {
			    thisLocaleLabels.put(content.getName(), content.getText());
			}
			logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "tempLocale: " + tempLocale + ", label: "
				+ content.getText());
		    }
		    localeLabels.put(tempLocale, thisLocaleLabels);
		}
	    }
	}
	return localeLabels.get(locale);
    }
    // public synchronized List<Object> getNavigationPages() {
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "getNavigationPages");
    // if (navigationPages == null) {
    // navigationPages = new ArrayList<Object>();
    // navigationPages.addAll(getNavigationPageWithChildren(pageDAO.findHomePage()));
    // }
    // return navigationPages;
    // }
    //
    // public synchronized void refreshNavigationPages() {
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "refreshNavigationPages");
    // navigationPages = new ArrayList<Object>();
    // navigationPages.addAll(getNavigationPageWithChildren(pageDAO.findHomePage()));
    // }

    // private List<Object> getNavigationPageWithChildren(Page page) {
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL,
    // "getNavigationPageWithChildren");
    // List<Object> result = new ArrayList<Object>();
    // if (page != null) {
    // PageDTO pageDTO = DozerBeanMapperSingletonWrapper.getInstance().map(page,
    // PageDTO.class);
    // Content descriptionContent =
    // contentDAO.findContentForLocale(page.getDescriptionContentId(),
    // ContentKindType.page_description, SessionUtils.getUserLocale());
    // if (descriptionContent != null) {
    // pageDTO.setDescriptionContent(DozerBeanMapperSingletonWrapper.getInstance().map(descriptionContent,
    // ContentDTO.class));
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL,
    // "getNavigationPageWithChildren description found: " +
    // pageDTO.getDescriptionContent().getName());
    // } else {
    // logger.log(Constants.SERVER_WARN_LOG_LEVEL,
    // "getNavigationPageWithChildren description not found");
    // }
    // result.add(pageDTO);
    // List<Page> childPages = pageDAO.findPages(page.getPageId(), true);
    // if (childPages != null) {
    // for(Page childPage: childPages) {
    // if (childPage != null) {
    // result.add(getNavigationPageWithChildren(childPage));
    // }
    // }
    // }
    // }
    // return result;
    // }

}
