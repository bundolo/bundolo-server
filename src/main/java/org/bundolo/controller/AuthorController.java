package org.bundolo.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
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

@Controller
public class AuthorController {

	private static final Logger logger = Logger.getLogger(AuthorController.class.getName());

	@Autowired
	private UserService userService;

	@Autowired
	private ContentService contentService;

	@RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{slug}", method = RequestMethod.GET)
	public @ResponseBody User author(@PathVariable String slug) {
		logger.log(Level.INFO, "author, slug: " + slug);
		return userService.findUser(ContentKindType.user_description.getLocalizedName() + "/" + slug);
	}

	@RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{slug}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean delete(@PathVariable String slug) {
		return userService.deleteUser(ContentKindType.user_description.getLocalizedName() + "/" + slug) != null;
	}

	@RequestMapping(value = Constants.REST_PATH_AUTH, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> auth(@RequestParam(required = true) String username,
			@RequestParam(required = true) String password) {
		logger.log(Level.INFO, "auth, username: " + username + ", password: " + password);
		return userService.authenticateUser(username, password);
	}

	@RequestMapping(value = Constants.REST_PATH_PASSWORD, method = RequestMethod.POST)
	public @ResponseBody ReturnMessageType password(@RequestParam(required = true) String username,
			@RequestParam(required = true) String email) {
		logger.log(Level.INFO, "password, username: " + username + ", email: " + email);
		return userService.sendNewPassword(username, email);
	}

	@RequestMapping(value = Constants.REST_PATH_AUTHOR, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveOrUpdate(@RequestBody final UserProfile userProfile) {
		logger.log(Level.INFO, "saveOrUpdate, userProfile: " + userProfile);
		ResponseEntity<String> result = userService.saveOrUpdateUser(userProfile);
		if (HttpStatus.OK.equals(result.getStatusCode())) {
			userService.clearSession();
		}
		return result;
	}

	@RequestMapping(value = Constants.REST_PATH_VALIDATE + "/{nonce}", method = RequestMethod.POST)
	public @ResponseBody ReturnMessageType validate(@PathVariable String nonce,
			@RequestParam(required = true) String email) {
		logger.log(Level.INFO, "validate, nonce: " + nonce + ", email: " + email);
		ReturnMessageType result = userService.activateUserEmailAddress(email, nonce);
		if (ReturnMessageType.success.equals(result)) {
			userService.clearSession();
		}
		return result;
	}

	@RequestMapping(value = Constants.REST_PATH_MESSAGE + "/" + Constants.REST_PATH_AUTHOR
			+ "/{slug}", method = RequestMethod.POST)
	public @ResponseBody ReturnMessageType message(@PathVariable String slug,
			@RequestBody final Map<String, String> message) {
		logger.log(Level.INFO, "message, slug: " + slug + ", message: " + message);
		return userService.sendMessage(message.get("title"), message.get("text"),
				ContentKindType.user_description.getLocalizedName() + "/" + slug);
	}

	@RequestMapping(value = Constants.REST_PATH_MESSAGE, method = RequestMethod.POST)
	public @ResponseBody ReturnMessageType messageToBundolo(@RequestBody final Map<String, String> message) {
		logger.log(Level.INFO, "messageToBundolo, message: " + message);
		return userService.sendMessage(message.get("title"), message.get("text"), null);
	}

	@RequestMapping(value = Constants.REST_PATH_RECOMMEND + "/" + Constants.REST_PATH_AUTHOR
			+ "/{slug}", method = RequestMethod.POST)
	public @ResponseBody ReturnMessageType message(@PathVariable String slug,
			@RequestParam(required = true) Long contentId) {
		logger.log(Level.INFO, "recommend, slug: " + slug + ", contentId: " + contentId);
		return userService.recommend(contentId, ContentKindType.user_description.getLocalizedName() + "/" + slug);
	}
}