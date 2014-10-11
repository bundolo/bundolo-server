package org.bundolo.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ReturnMessageType;
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

    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Connection connection(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	logger.log(Level.WARNING, "connection, restOfTheUrl: " + restOfTheUrl);
	return connectionService.findConnection(restOfTheUrl.substring(Constants.REST_PATH_CONNECTION.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/**", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean delete(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	logger.log(Level.WARNING, "delete connection, restOfTheUrl: " + restOfTheUrl);
	return connectionService.deleteConnection(restOfTheUrl.substring(Constants.REST_PATH_CONNECTION.length() + 1)) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    ReturnMessageType saveOrUpdate(@PathVariable String title, @RequestBody final Connection connection) {
	logger.log(Level.WARNING, "saveOrUpdate, connection: " + connection);
	if (!title.matches(Constants.URL_SAFE_REGEX)) {
	    return ReturnMessageType.title_not_url_safe;
	}
	connection.getDescriptionContent().setName(title.trim());
	ReturnMessageType result = connectionService.saveOrUpdateConnection(connection);
	if (ReturnMessageType.success.equals(result)) {
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