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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
	Boolean result = contentService.saveOrUpdateContent(topic);
	if (result) {
	    contentService.clearSession();
	}
	return result;
    }

}