package org.bundolo.controller;

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
public class SerialController {

    private static final Logger logger = Logger.getLogger(SerialController.class.getName());

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Content serial(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	// TODO check param validity
	return contentService.findSerial(restOfTheUrl.substring(Constants.REST_PATH_SERIAL.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_SERIAL + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdate(@PathVariable String title, @RequestBody final Content serial) {
	logger.log(Level.WARNING, "saveOrUpdate, serial: " + serial);
	// TODO check param validity
	serial.setKind(ContentKindType.episode_group);
	Boolean result = contentService.saveOrUpdateContent(serial);
	if (result) {
	    contentService.clearSession();
	}
	return result;
    }

}