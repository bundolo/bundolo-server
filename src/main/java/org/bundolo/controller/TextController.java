package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.services.CommentService;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TextController {

	private static final Logger logger = Logger.getLogger(TextController.class.getName());

	@Autowired
	private ContentService contentService;

	@Autowired
	private CommentService commentService;

	@RequestMapping(value = Constants.REST_PATH_TEXT + "/{authorSlug}/{textSlug}", method = RequestMethod.GET)
	public @ResponseBody Content text(@PathVariable String authorSlug, @PathVariable String textSlug) {
		logger.log(Level.INFO, "text, authorSlug: " + authorSlug + ", textSlug: " + textSlug);
		return contentService.findText(ContentKindType.text.getLocalizedName() + "/" + authorSlug + "/" + textSlug);
	}

	@RequestMapping(value = Constants.REST_PATH_TEXT + "/{authorSlug}/{textSlug}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean delete(@PathVariable String authorSlug, @PathVariable String textSlug) {
		logger.log(Level.INFO, "delete, authorSlug: " + authorSlug + ", textSlug: " + textSlug);
		Long textId = contentService
				.deleteText(ContentKindType.text.getLocalizedName() + "/" + authorSlug + "/" + textSlug);
		Boolean result = textId != null;
		if (result) {
			// contentService.clearSession();
			commentService.deleteCommentsByParentId(textId);
			// commentService.clearSession();
		}
		return result;
	}

	@RequestMapping(value = Constants.REST_PATH_TEXT, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveOrUpdate(@RequestBody final Content text) {
		logger.log(Level.INFO, "saveOrUpdate, text: " + text);
		text.setKind(ContentKindType.text);
		ResponseEntity<String> result = contentService.saveOrUpdateContent(text, false);
		if (HttpStatus.OK.equals(result.getStatusCode())) {
			contentService.clearSession();
		}
		return result;
	}

	@RequestMapping(value = Constants.REST_PATH_VERIFY + "/" + Constants.REST_PATH_TEXT, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> verify() {
		return contentService.verify();
	}

}