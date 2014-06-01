package org.bundolo.controller;

import org.bundolo.Constants;
import org.bundolo.model.Contest;
import org.bundolo.services.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ContestController {

    @Autowired
    private ContestService contestService;

    @RequestMapping(value = Constants.REST_PATH_CONTEST + "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Contest contest(@PathVariable Long id) {
	// TODO check param validity
	return contestService.findContest(id);
    }

}