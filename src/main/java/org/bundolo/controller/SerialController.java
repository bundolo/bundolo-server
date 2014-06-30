package org.bundolo.controller;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Content;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class SerialController {

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Content serial(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	// TODO check param validity
	return contentService.findSerial(restOfTheUrl.substring(Constants.REST_PATH_SERIAL.length() + 1));
    }

}