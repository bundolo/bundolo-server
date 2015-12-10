package org.bundolo.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.services.ConnectionService;
import org.bundolo.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConnectionController {

    private static final Logger logger = Logger.getLogger(ConnectionController.class.getName());

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/{slug}", method = RequestMethod.GET)
    public @ResponseBody
    Connection connection(@PathVariable String slug) {
	logger.log(Level.INFO, "connection, slug: " + slug);
	return connectionService.findConnection(ContentKindType.connection_description.getLocalizedName() + "/" + slug);
    }

    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/{slug}", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean delete(@PathVariable String slug) {
	logger.log(Level.INFO, "delete connection, slug: " + slug);
	return connectionService.deleteConnection(ContentKindType.connection_description.getLocalizedName() + "/"
		+ slug) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_CONNECTION, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> saveOrUpdate(@RequestBody final Connection connection) {
	logger.log(Level.INFO, "saveOrUpdate, connection: " + connection);
	ResponseEntity<String> result = connectionService.saveOrUpdateConnection(connection);
	if (HttpStatus.OK.equals(result.getStatusCode())) {
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