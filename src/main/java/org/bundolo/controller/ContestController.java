package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.model.Contest;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.bundolo.services.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class ContestController {

    private static final Logger logger = Logger.getLogger(ContestController.class.getName());

    @Autowired
    private ContestService contestService;

    @RequestMapping(value = Constants.REST_PATH_CONTEST + "/**", method = RequestMethod.GET)
    public @ResponseBody
    Contest contest(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	restOfTheUrl = SecurityUtils.removeBotSuffix(restOfTheUrl);
	return contestService.findContest(restOfTheUrl.substring(Constants.REST_PATH_CONTEST.length() + 1));
    }

    @RequestMapping(value = Constants.REST_PATH_CONTEST + "/**", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean delete(HttpServletRequest request) {
	String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	return contestService.deleteContest(restOfTheUrl.substring(Constants.REST_PATH_CONTEST.length() + 1)) != null;
    }

    @RequestMapping(value = Constants.REST_PATH_CONTEST + "/{title}", method = RequestMethod.PUT)
    public @ResponseBody
    ReturnMessageType saveOrUpdate(@PathVariable String title, @RequestBody final Contest contest) {
	logger.log(Level.INFO, "saveOrUpdate, contest: " + contest);
	if (!title.matches(Constants.URL_SAFE_REGEX)) {
	    return ReturnMessageType.title_not_url_safe;
	}
	contest.getDescriptionContent().setName(title.trim());
	ReturnMessageType result = contestService.saveOrUpdateContest(contest);
	if (ReturnMessageType.success.equals(result)) {
	    contestService.clearSession();
	}
	return result;
    }

}