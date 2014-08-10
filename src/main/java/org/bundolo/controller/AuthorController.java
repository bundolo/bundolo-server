package org.bundolo.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.services.ContentService;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthorController {

    private static final Logger logger = Logger.getLogger(AuthorController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{username}", method = RequestMethod.GET)
    public @ResponseBody
    User author(@PathVariable String username) {
	// TODO check param validity
	return userService.findUser(username);
    }

    @RequestMapping(value = Constants.REST_PATH_AUTH + "/{username}", method = RequestMethod.POST)
    public @ResponseBody
    Boolean auth(@PathVariable String username, @RequestParam String password) {
	// TODO check param validity
	return userService.authenticateUser(username, password);
    }

    @RequestMapping(value = Constants.REST_PATH_PASSWORD + "/{username}", method = RequestMethod.POST)
    public @ResponseBody
    Boolean password(@PathVariable String username, @RequestParam String email) {
	// TODO check param validity
	return userService.sendNewPassword(username, email);
    }

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{username}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdate(@PathVariable String username, @RequestBody final UserProfile userProfile) {
	logger.log(Level.WARNING, "saveOrUpdate, userProfile: " + userProfile);
	// TODO check param validity
	// TODO security checks
	userProfile.setUsername(username);
	Boolean result = userService.saveOrUpdateUser(userProfile);
	if (result) {
	    userService.clearSession();
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_VALIDATE + "/{nonce}", method = RequestMethod.POST)
    public @ResponseBody
    Boolean validate(@PathVariable String nonce, @RequestParam String email) {
	logger.log(Level.WARNING, "activate, nonce: " + nonce + ", email: " + email);
	// TODO check param validity
	Boolean result = userService.activateUserEmailAddress(email, nonce);
	if (result) {
	    userService.clearSession();
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_STATISTICS + "/{username}", method = RequestMethod.GET)
    public @ResponseBody
    List<Content> statistics(@PathVariable String username) {
	// TODO check param validity
	return contentService.findStatistics(username);
    }

}