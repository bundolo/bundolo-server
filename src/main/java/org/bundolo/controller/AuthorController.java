package org.bundolo.controller;

import org.bundolo.Constants;
import org.bundolo.model.UserProfile;
import org.bundolo.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthorController {

    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping(value = Constants.REST_PATH_AUTHOR + "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    UserProfile author(@PathVariable Long id) {
	// TODO check param validity
	return userProfileService.findUserProfile(id);
    }

}