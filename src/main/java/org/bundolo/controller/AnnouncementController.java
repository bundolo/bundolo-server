package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
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
public class AnnouncementController {

	private static final Logger logger = Logger.getLogger(AnnouncementController.class.getName());

	@Autowired
	private ContentService contentService;

	@RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/{slug}", method = RequestMethod.GET)
	@ResponseBody
	public Content announcement(@PathVariable String slug) {
		logger.log(Level.INFO, "announcement, slug: " + slug);
		return contentService.findAnnouncement(ContentKindType.news.getLocalizedName() + "/" + slug);
	}

	@RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT + "/{slug}", method = RequestMethod.DELETE)
	@ResponseBody
	public Boolean delete(@PathVariable String slug) {
		logger.log(Level.INFO, "delete announcement, slug: " + slug);
		return contentService.deleteAnnouncement(ContentKindType.news.getLocalizedName() + "/" + slug) != null;
	}

	@RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENT, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveOrUpdate(@RequestBody final Content announcement) {
		logger.log(Level.INFO, "saveOrUpdate, announcement: " + announcement);
		announcement.setKind(ContentKindType.news);
		ResponseEntity<String> result = contentService.saveOrUpdateContent(announcement, false);
		if (HttpStatus.OK.equals(result.getStatusCode())) {
			contentService.clearSession();
		}
		return result;
	}
}