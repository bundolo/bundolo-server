package org.bundolo.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.services.ConnectionService;
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
public class ConnectionController {

    private static final Logger logger = Logger.getLogger(ConnectionController.class.getName());

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ContentService contentService;

    // @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/{title:.+/}", method = RequestMethod.GET)
    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Connection connection(/*@PathVariable String title, */HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	logger.log(Level.WARNING, "connection, restOfTheUrl: " + restOfTheUrl);

	// TODO check param validity
	return connectionService.findConnection(restOfTheUrl.substring(Constants.REST_PATH_CONNECTION.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean saveOrUpdate(@PathVariable String title, @RequestBody final Connection connection) {
	logger.log(Level.WARNING, "saveOrUpdate, connection: " + connection);
	// TODO check param validity
	Boolean result = connectionService.saveOrUpdateConnection(connection);
	if (result) {
	    connectionService.clearSession();
	}
	return result;
    }

    @RequestMapping(value = Constants.REST_PATH_CONNECTION_GROUPS, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> connectionGroups() {
	return contentService.findConnectionGroups();
    }

}