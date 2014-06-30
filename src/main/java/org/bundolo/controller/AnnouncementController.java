package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class AnnouncementController {

    private static final Logger logger = Logger.getLogger(AnnouncementController.class.getName());

    @Autowired
    private ContentService contentService;

    // @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/{title}", method = RequestMethod.GET)
    // public @ResponseBody
    // Content announcement(@PathVariable String title) {
    // // TODO check param validity
    // return contentService.findAnnouncement(title);
    // }

    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Content announcement(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	logger.log(Level.WARNING, "connection, restOfTheUrl: " + restOfTheUrl);

	// TODO check param validity
	return contentService.findAnnouncement(restOfTheUrl.substring(Constants.REST_PATH_ANNOUNCEMENT.length() + 1));
    }

    /*
    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdate(@PathVariable String id, @RequestBody final UserProfile userProfile) {
    logger.log(Level.WARNING, "saveOrUpdate, userProfile: " + userProfile);
    // TODO check param validity
    // TODO security checks
    userProfile.setUsername(username);
    Boolean result = userService.saveOrUpdateUser(userProfile);
    if (result) {
        userService.clearSession();
    }
    return result;
    }*/

}