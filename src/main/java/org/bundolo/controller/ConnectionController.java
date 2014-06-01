package org.bundolo.controller;

import org.bundolo.Constants;
import org.bundolo.model.Connection;
import org.bundolo.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @RequestMapping(value = Constants.REST_PATH_CONNECTION + "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Connection connection(@PathVariable Long id) {
	// TODO check param validity
	return connectionService.findConnection(id);
    }

}