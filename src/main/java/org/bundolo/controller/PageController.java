package org.bundolo.controller;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.PageKindType;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {

    private static final Logger logger = Logger.getLogger(PageController.class.getName());

    @Autowired
    private ContentService contentService;

    // TODO this should return complete page in the future, not just description
    @RequestMapping(value = Constants.REST_PATH_PAGE + "/{pageKind}", method = RequestMethod.GET)
    public @ResponseBody
    Content page(@PathVariable PageKindType pageKind) {
	// logger.log(Level.WARNING, "page, pageKind: " + pageKind);
	return contentService.getPageDescriptionContent(pageKind);
    }

    // TODO backlog: define and retrieve metrics
    // take date range
    @RequestMapping(value = Constants.REST_PATH_METRICS + "/{pageKind}", method = RequestMethod.GET)
    public @ResponseBody
    HashMap metrics(@PathVariable PageKindType pageKind) {
	return null;
    }

}