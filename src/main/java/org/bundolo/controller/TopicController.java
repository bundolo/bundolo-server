package org.bundolo.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
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
	// TODO check param validity
	return contentService.findTopic(restOfTheUrl.substring(Constants.REST_PATH_TOPIC.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_TOPIC_GROUPS, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> topicGroups() {
	return contentService.findTopicGroups();
    }

    @RequestMapping(value = Constants.REST_PATH_TOPIC + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdate(@PathVariable String title, @RequestBody final Content topic) {
	logger.log(Level.WARNING, "saveOrUpdate, topic: " + topic);
	// TODO check param validity
	topic.setKind(ContentKindType.forum_topic);
	topic.setName(title);
	Boolean result = contentService.saveOrUpdateContent(topic, true);
	if (result) {
	    contentService.clearSession();
	}
	return result;
    }

    @RequestMapping(Constants.REST_PATH_POSTS)
    public @ResponseBody
    List<Content> topics(@RequestParam(required = true) Long parentId,
	    @RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "-1") Integer end) {
	// TODO check param validity
	return contentService.findPosts(parentId, start, end);
    }

    // TODO this should eventually become put method, to avoid saving the same post twice, but it's going to be a
    // problem finding unique url format for them
    @RequestMapping(value = Constants.REST_PATH_POST, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Boolean save(@RequestBody final Content post) {
	logger.log(Level.WARNING, "saving post: " + post);
	// TODO check param validity
	post.setKind(ContentKindType.forum_post);
	Boolean result = contentService.saveOrUpdateContent(post, true);
	if (result) {
	    contentService.clearSession();
	}
	return result;
    }

}