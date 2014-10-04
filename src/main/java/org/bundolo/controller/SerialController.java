package org.bundolo.controller;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.services.CommentService;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class SerialController {

    private static final Logger logger = Logger.getLogger(SerialController.class.getName());

    @Autowired
    private ContentService contentService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Content serial(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	// TODO check param validity
	return contentService.findSerial(restOfTheUrl.substring(Constants.REST_PATH_SERIAL.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/**", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean deleteSerial(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	// TODO check param validity
	return contentService.deleteSerial(restOfTheUrl.substring(Constants.REST_PATH_SERIAL.length() + 1)) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdate(@PathVariable String title, @RequestBody final Content serial) {
	logger.log(Level.WARNING, "saveOrUpdate, serial: " + serial);
	// TODO check param validity
	serial.setKind(ContentKindType.episode_group);
	serial.setName(title);
	Boolean result = contentService.saveOrUpdateContent(serial, false);
	if (result) {
	    contentService.clearSession();
	}
	return result;
    }

    @RequestMapping(Constants.REST_PATH_EPISODES)
    public @ResponseBody
    List<Content> episodes(@RequestParam(required = true) Long parentId,
	    @RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "-1") Integer end) {
	// TODO check param validity
	return contentService.findEpisodes(parentId, start, end);
    }

    @RequestMapping(value = Constants.REST_PATH_EPISODE + "/{serialTitle}/**", method = RequestMethod.GET)
    public @ResponseBody
    Content episode(@PathVariable String serialTitle, HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	restOfTheUrl = restOfTheUrl.substring(restOfTheUrl.indexOf(serialTitle));
	// TODO check param validity
	return contentService.findEpisode(serialTitle, restOfTheUrl.substring(serialTitle.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_EPISODE + "/{serialTitle}/**", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean deleteEpisode(@PathVariable String serialTitle, HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	restOfTheUrl = restOfTheUrl.substring(restOfTheUrl.indexOf(serialTitle));
	// TODO check param validity
	Long episodeId = contentService.deleteEpisode(serialTitle, restOfTheUrl.substring(serialTitle.length() + 1));
	Boolean result = episodeId != null;
	if (result) {
	    // contentService.clearSession();
	    commentService.deleteCommentsByParentId(episodeId);
	    // commentService.clearSession();
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_EPISODE + "/{serialTitle}/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdateEpisode(@PathVariable String serialTitle, @PathVariable String title,
	    @RequestBody final Content episode) {
	logger.log(Level.WARNING, "saveOrUpdate, episode: " + episode);
	// TODO check param validity
	Content serial = contentService.findSerial(serialTitle);
	episode.getParentContent().setContentId(serial.getContentId());
	episode.getParentContent().setName(serial.getName());
	Date creationDate = new Date();
	episode.setLastActivity(creationDate);
	episode.setKind(ContentKindType.episode);
	episode.setName(title);
	Boolean result = contentService.saveOrUpdateContent(episode, false);
	if (result) {
	    contentService.updateLastActivity(episode.getParentContent().getContentId(), creationDate);
	    contentService.clearSession();
	}
	return result;
    }

}