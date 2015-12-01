package org.bundolo.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.model.Content;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ContentKindType;
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
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class AuthorController {

    private static final Logger logger = Logger.getLogger(AuthorController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{slug}", method = RequestMethod.GET)
    public @ResponseBody
    User author(@PathVariable String slug) {
	logger.log(Level.WARNING, "author, slug: " + slug);
	return userService.findUser(ContentKindType.user_description.getLocalizedName() + "/" + slug);
    }

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/**", method = RequestMethod.GET)
    public @ResponseBody
    User author(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	restOfTheUrl = SecurityUtils.removeBotSuffix(restOfTheUrl);
	logger.log(Level.WARNING, "author, restOfTheUrl: " + restOfTheUrl);
	Content content = contentService.findContent(restOfTheUrl.substring(Constants.REST_PATH_AUTHOR.length() + 1));
	return userService.findUserByUsername(content.getAuthorUsername());
    }

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{slug}", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean delete(@PathVariable String slug) {
	return userService.deleteUser(ContentKindType.user_description.getLocalizedName() + "/" + slug) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_AUTH, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> auth(@RequestParam(required = true) String username,
	    @RequestParam(required = true) String password) {
	logger.log(Level.INFO, "auth, username: " + username + ", password: " + password);
	return userService.authenticateUser(username, password);
    }

    @RequestMapping(value = Constants.REST_PATH_PASSWORD + "/{slug}", method = RequestMethod.POST)
    public @ResponseBody
    ReturnMessageType password(@PathVariable String slug, @RequestParam(required = true) String email) {
	logger.log(Level.INFO, "password, slug: " + slug + ", email: " + email);
	return userService.sendNewPassword(slug, email);
    }

    @RequestMapping(value = Constants.REST_PATH_AUTHOR, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> saveOrUpdate(@RequestBody final UserProfile userProfile) {
	logger.log(Level.INFO, "saveOrUpdate, userProfile: " + userProfile);
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
    ReturnMessageType message(@PathVariable String slug, @RequestBody final Map<String, String> message) {
	logger.log(Level.INFO, "message, slug: " + slug + ", message: " + message);
	return userService.sendMessage(message.get("title"), message.get("text"), slug);
    }

    @RequestMapping(value = Constants.REST_PATH_MESSAGE, method = RequestMethod.POST)
    public @ResponseBody
    ReturnMessageType messageToBundolo(@RequestBody final Map<String, String> message) {
	logger.log(Level.INFO, "messageToBundolo, message: " + message);
	return userService.sendMessage(message.get("title"), message.get("text"), null);
    }
}