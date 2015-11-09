package org.bundolo.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.bundolo.services.ContentService;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{username:.+}", method = RequestMethod.GET)
    public @ResponseBody
    User author(@PathVariable String username) {
	username = SecurityUtils.removeBotSuffix(username);
	logger.log(Level.INFO, "author, username: " + username);
	return userService.findUser(username);
    }

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{username:.+}", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean delete(@PathVariable String username) {
	return userService.deleteUser(username) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_AUTH + "/{username:.+}", method = RequestMethod.POST)
    public @ResponseBody
    ReturnMessageType auth(@PathVariable String username, @RequestParam(required = true) String password) {
	return userService.authenticateUser(username, password);
    }

    @RequestMapping(value = Constants.REST_PATH_PASSWORD + "/{username:.+}", method = RequestMethod.POST)
    public @ResponseBody
    ReturnMessageType password(@PathVariable String username, @RequestParam(required = true) String email) {
	logger.log(Level.INFO, "password, username: " + username + ", email: " + email);
	return userService.sendNewPassword(username, email);
    }

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{username:.+}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<String> saveOrUpdate(@PathVariable String username, @RequestBody final UserProfile userProfile) {
	logger.log(Level.INFO, "saveOrUpdate, userProfile: " + userProfile);
	if (!username.matches(Constants.USERNAME_SAFE_REGEX)) {
	    return new ResponseEntity<String>(ReturnMessageType.username_not_url_safe.name(), HttpStatus.BAD_REQUEST);
	}
	userProfile.setUsername(username.trim());
	ResponseEntity<String> result = userService.saveOrUpdateUser(userProfile);
	if (HttpStatus.OK.equals(result)) {
	    userService.clearSession();
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_VALIDATE + "/{nonce}", method = RequestMethod.POST)
    public @ResponseBody
    ReturnMessageType validate(@PathVariable String nonce, @RequestParam(required = true) String email) {
	logger.log(Level.INFO, "activate, nonce: " + nonce + ", email: " + email);
	ReturnMessageType result = userService.activateUserEmailAddress(email, nonce);
	if (ReturnMessageType.success.equals(result)) {
	    userService.clearSession();
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_MESSAGE + "/{username:.+}", method = RequestMethod.POST)
    public @ResponseBody
    ReturnMessageType message(@PathVariable String username, @RequestBody final Map<String, String> message) {
	logger.log(Level.INFO, "message, username: " + username + ", message: " + message);
	return userService.sendMessage(message.get("title"), message.get("text"), username);
    }

    @RequestMapping(value = Constants.REST_PATH_MESSAGE, method = RequestMethod.POST)
    public @ResponseBody
    ReturnMessageType messageToBundolo(@RequestBody final Map<String, String> message) {
	logger.log(Level.INFO, "messageToBundolo, message: " + message);
	return userService.sendMessage(message.get("title"), message.get("text"), null);
    }
}