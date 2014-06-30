package org.bundolo.controller;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.model.Contest;
import org.bundolo.services.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class ContestController {

    @Autowired
    private ContestService contestService;

    @RequestMapping(value = Constants.REST_PATH_CONTEST + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Contest contest(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	// TODO check param validity
	return contestService.findContest(restOfTheUrl.substring(Constants.REST_PATH_CONTEST.length() + 1));
    }

}