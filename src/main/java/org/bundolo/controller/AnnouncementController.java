package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ReturnMessageType;
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
    @ResponseBody
    public Content announcement(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	logger.log(Level.WARNING, "announcement, restOfTheUrl: " + restOfTheUrl);
	return contentService.findAnnouncement(restOfTheUrl.substring(Constants.REST_PATH_ANNOUNCEMENT.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/**", method = RequestMethod.DELETE)
    @ResponseBody
    public Boolean delete(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	logger.log(Level.WARNING, "delete announcement, restOfTheUrl: " + restOfTheUrl);
	return contentService.deleteAnnouncement(restOfTheUrl.substring(Constants.REST_PATH_ANNOUNCEMENT.length() + 1)) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    ReturnMessageType saveOrUpdate(@PathVariable String title, @RequestBody final Content announcement) {
	logger.log(Level.WARNING, "saveOrUpdate, announcement: " + announcement);
	if (!title.matches(Constants.URL_SAFE_REGEX)) {
	    return ReturnMessageType.title_not_url_safe;
	}
	announcement.setName(title.trim());
	announcement.setKind(ContentKindType.news);
	ReturnMessageType result = contentService.saveOrUpdateContent(announcement, false);
	if (ReturnMessageType.success.equals(result)) {
	    contentService.clearSession();
	}
	return result;
    }
}