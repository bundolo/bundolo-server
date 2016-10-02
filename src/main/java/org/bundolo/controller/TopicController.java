package org.bundolo.controller;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.DateUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class TopicController {

	private static final Logger logger = Logger.getLogger(TopicController.class.getName());

	@Autowired
	private ContentService contentService;

	@Autowired
	private DateUtils dateUtils;

	@RequestMapping(value = Constants.REST_PATH_TOPIC + "/{slug}", method = RequestMethod.GET)
	public @ResponseBody Content topic(@PathVariable String slug) {
		return contentService.findTopic(ContentKindType.forum_topic.getLocalizedName() + "/" + slug);
	}

	@RequestMapping(value = Constants.REST_PATH_TOPIC + "/{slug}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean delete(@PathVariable String slug) {
		return contentService.deleteTopic(ContentKindType.forum_topic.getLocalizedName() + "/" + slug) != null;
	}

	@RequestMapping(value = Constants.REST_PATH_TOPIC_GROUPS, method = RequestMethod.GET)
	public @ResponseBody List<Content> topicGroups() {
		return contentService.findTopicGroups();
	}

	@RequestMapping(value = Constants.REST_PATH_TOPIC, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveOrUpdate(@RequestBody final Content topic) {
		logger.log(Level.INFO, "saveOrUpdate, topic: " + topic);
		topic.setKind(ContentKindType.forum_topic);
		ResponseEntity<String> result = contentService.saveOrUpdateContent(topic, true);
		if (HttpStatus.OK.equals(result)) {
			contentService.clearSession();
		}
		return result;
	}

	@RequestMapping(Constants.REST_PATH_POSTS)
	public @ResponseBody List<Content> posts(@RequestParam(required = true) Long parentId,
			@RequestParam(required = false, defaultValue = "0") Integer start,
			@RequestParam(required = false, defaultValue = "-1") Integer end) {
		return contentService.findPosts(parentId, start, end);
	}

	// TODO this should eventually become put method, to avoid saving the same
	// post twice, but it's going to be a
	// problem finding unique url format for them
	// TODO see why is there @ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = Constants.REST_PATH_POST, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody ResponseEntity<String> save(@RequestBody final Content post) {
		logger.log(Level.INFO, "saving post: " + post);
		Date creationDate = dateUtils.newDate();
		post.setLastActivity(creationDate);
		post.setKind(ContentKindType.forum_post);
		ResponseEntity<String> result = contentService.saveOrUpdateContent(post, true);
		if (HttpStatus.OK.equals(result.getStatusCode())) {
			contentService.updateLastActivity(post.getParentContent().getContentId(), creationDate);
			contentService.clearSession();
		}
		return result;
	}

}