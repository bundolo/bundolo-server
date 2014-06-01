package org.bundolo.controller;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TopicController {

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_TOPIC + "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Content topic(@PathVariable Long id) {
	// TODO check param validity
	return contentService.findContent(id);
    }

}