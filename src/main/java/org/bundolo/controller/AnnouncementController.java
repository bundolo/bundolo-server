package org.bundolo.controller;

import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AnnouncementController {

    private static final Logger logger = Logger.getLogger(AnnouncementController.class.getName());

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/{title}", method = RequestMethod.GET)
    public @ResponseBody
    Content announcement(@PathVariable String title) {
	// TODO check param validity
	return contentService.findAnnouncement(title);
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