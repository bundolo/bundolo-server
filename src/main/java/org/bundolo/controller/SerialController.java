package org.bundolo.controller;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.DateUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SerialController {

    private static final Logger logger = Logger.getLogger(SerialController.class.getName());

    @Autowired
    private ContentService contentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DateUtils dateUtils;

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/{slug}", method = RequestMethod.GET)
    public @ResponseBody
    Content serial(@PathVariable String slug) {
	return contentService.findSerial(ContentKindType.episode_group.getLocalizedName() + "/" + slug);
    }

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/" + Constants.REST_PATH_EPISODE
	    + "/{serialSlug}/{episodeSlug}", method = RequestMethod.GET)
    public @ResponseBody
    Content serialByEpisode(@PathVariable String serialSlug, @PathVariable String episodeSlug) {
	Content episode = contentService.findContent(ContentKindType.episode.getLocalizedName() + "/" + serialSlug
		+ "/" + episodeSlug);
	return contentService.findSerial(episode.getParentContent().getSlug());
    }

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/{slug}", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean deleteSerial(@PathVariable String slug) {
	return contentService.deleteSerial(ContentKindType.episode_group.getLocalizedName() + "/" + slug) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_SERIAL, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> saveOrUpdate(@RequestBody final Content serial) {
	logger.log(Level.INFO, "saveOrUpdate, serial: " + serial);
	serial.setKind(ContentKindType.episode_group);
	ResponseEntity<String> result = contentService.saveOrUpdateContent(serial, false);
	if (HttpStatus.OK.equals(result)) {
	    contentService.clearSession();
	}
	return result;
    }

    @RequestMapping(Constants.REST_PATH_EPISODES)
    public @ResponseBody
    List<Content> episodes(@RequestParam(required = true) Long parentId,
	    @RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "-1") Integer end) {
	return contentService.findEpisodes(parentId, start, end);
    }

    @RequestMapping(value = Constants.REST_PATH_EPISODE + "/{serialSlug}/{episodeSlug}", method = RequestMethod.GET)
    public @ResponseBody
    Content episode(@PathVariable String serialSlug, @PathVariable String episodeSlug) {
	return contentService.findEpisode(ContentKindType.episode.getLocalizedName() + "/" + serialSlug + "/"
		+ episodeSlug);
    }

    @RequestMapping(value = Constants.REST_PATH_EPISODE + "/{serialSlug}/{episodeSlug}", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean deleteEpisode(@PathVariable String serialSlug, @PathVariable String episodeSlug) {
	Long episodeId = contentService.deleteEpisode(ContentKindType.episode.getLocalizedName() + "/" + serialSlug
		+ "/" + episodeSlug);
	Boolean result = episodeId != null;
	if (result) {
	    // contentService.clearSession();
	    commentService.deleteCommentsByParentId(episodeId);
	    // commentService.clearSession();
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_EPISODE, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> saveOrUpdateEpisode(@RequestBody final Content episode) {
	logger.log(Level.INFO, "saveOrUpdate, episode: " + episode);
	Date creationDate = dateUtils.newDate();
	episode.setLastActivity(creationDate);
	episode.setKind(ContentKindType.episode);
	ResponseEntity<String> result = contentService.saveOrUpdateContent(episode, false);
	if (HttpStatus.OK.equals(result)) {
	    contentService.updateLastActivity(episode.getParentContent().getContentId(), creationDate);
	    contentService.clearSession();
	}
	return result;
    }

}