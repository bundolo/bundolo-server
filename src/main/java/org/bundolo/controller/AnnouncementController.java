package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class AnnouncementController {

    private static final Logger logger = Logger.getLogger(AnnouncementController.class.getName());

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Content announcement(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	logger.log(Level.WARNING, "connection, restOfTheUrl: " + restOfTheUrl);

	// TODO check param validity
	return contentService.findAnnouncement(restOfTheUrl.substring(Constants.REST_PATH_ANNOUNCEMENT.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdate(@PathVariable String title, @RequestBody final Content announcement) {
	logger.log(Level.WARNING, "saveOrUpdate, announcement: " + announcement);
	// TODO check param validity
	announcement.setName(title);
	announcement.setKind(ContentKindType.news);
	Boolean result = contentService.saveOrUpdateContent(announcement, false);
	if (result) {
	    contentService.clearSession();
	}
	return result;
    }

}