package org.bundolo.controller;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class TopicController {

    private static final Logger logger = Logger.getLogger(TopicController.class.getName());

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_TOPIC + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Content topic(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	return contentService.findTopic(restOfTheUrl.substring(Constants.REST_PATH_TOPIC.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_TOPIC + "/**", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean delete(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	return contentService.deleteTopic(restOfTheUrl.substring(Constants.REST_PATH_TOPIC.length() + 1)) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_TOPIC_GROUPS, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> topicGroups() {
	return contentService.findTopicGroups();
    }

    @RequestMapping(value = Constants.REST_PATH_TOPIC + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    ReturnMessageType saveOrUpdate(@PathVariable String title, @RequestBody final Content topic) {
	logger.log(Level.INFO, "saveOrUpdate, topic: " + topic);
	if (!title.matches(Constants.URL_SAFE_REGEX)) {
	    return ReturnMessageType.title_not_url_safe;
	}
	topic.setKind(ContentKindType.forum_topic);
	topic.setName(title.trim());
	ReturnMessageType result = contentService.saveOrUpdateContent(topic, true);
	if (ReturnMessageType.success.equals(result)) {
	    contentService.clearSession();
	}
	return result;
    }

    @RequestMapping(Constants.REST_PATH_POSTS)
    public @ResponseBody
    List<Content> posts(@RequestParam(required = true) Long parentId,
	    @RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "-1") Integer end) {
	return contentService.findPosts(parentId, start, end);
    }

    // TODO this should eventually become put method, to avoid saving the same post twice, but it's going to be a
    // problem finding unique url format for them
    @RequestMapping(value = Constants.REST_PATH_POST, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ReturnMessageType save(@RequestBody final Content post) {
	logger.log(Level.INFO, "saving post: " + post);
	Date creationDate = new Date();
	post.setLastActivity(creationDate);
	post.setKind(ContentKindType.forum_post);
	ReturnMessageType result = contentService.saveOrUpdateContent(post, true);
	if (ReturnMessageType.success.equals(result)) {
	    contentService.updateLastActivity(post.getParentContent().getContentId(), creationDate);
	    contentService.clearSession();
	}
	return result;
    }

}