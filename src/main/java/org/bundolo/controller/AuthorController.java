package org.bundolo.controller;

import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.User;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthorController {

    private static final Logger logger = Logger.getLogger(AuthorController.class.getName());

    @Autowired
    private UserService userService;

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{username}", method = RequestMethod.GET)
    public @ResponseBody
    User author(@PathVariable String username) {
	// TODO check param validity
	return userService.findUser(username);
    }

    @RequestMapping(value = Constants.REST_PATH_AUTH + "/{username}", method = RequestMethod.POST)
    public @ResponseBody
    Boolean auth(@PathVariable String username, @RequestParam String password) {
	// TODO check param validity
	return userService.authenticateUser(username, password);
    }

}