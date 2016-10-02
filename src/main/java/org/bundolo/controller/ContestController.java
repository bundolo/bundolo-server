package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Contest;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.services.ContestService;
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
public class ContestController {

	private static final Logger logger = Logger.getLogger(ContestController.class.getName());

	@Autowired
	private ContestService contestService;

	@RequestMapping(value = Constants.REST_PATH_CONTEST + "/{slug}", method = RequestMethod.GET)
	public @ResponseBody Contest contest(@PathVariable String slug) {
		return contestService.findContest(ContentKindType.contest_description.getLocalizedName() + "/" + slug);
	}

	@RequestMapping(value = Constants.REST_PATH_CONTEST + "{slug}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean delete(@PathVariable String slug) {
		return contestService
				.deleteContest(ContentKindType.contest_description.getLocalizedName() + "/" + slug) != null;
	}

	@RequestMapping(value = Constants.REST_PATH_CONTEST, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveOrUpdate(@RequestBody final Contest contest) {
		logger.log(Level.INFO, "saveOrUpdate, contest: " + contest);
		ResponseEntity<String> result = contestService.saveOrUpdateContest(contest);
		if (HttpStatus.OK.equals(result.getStatusCode())) {
			contestService.clearSession();
		}
		return result;
	}

}