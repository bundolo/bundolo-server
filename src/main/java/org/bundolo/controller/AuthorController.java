package org.bundolo.controller;

import org.bundolo.Constants;
import org.bundolo.model.User;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthorController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{username}", method = RequestMethod.GET)
    public @ResponseBody
    User author(@PathVariable String username) {
	// TODO check param validity
	return userService.findUser(username);
    }

}