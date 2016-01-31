package org.bundolo.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.bundolo.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RatingController {

    private static final Logger logger = Logger.getLogger(RatingController.class.getName());

    @Autowired
    private RatingService ratingService;

    @RequestMapping(value = Constants.REST_PATH_RATING + "/{contentId}", method = RequestMethod.GET)
    public @ResponseBody
    Rating rating(@PathVariable Long contentId) {
	return ratingService.findPersonalRating(contentId);
    }

    @RequestMapping(value = Constants.REST_PATH_RATING + "/{ratingId}", method = RequestMethod.PUT)
    public @ResponseBody
    ReturnMessageType update(@PathVariable Long ratingId, @RequestBody Rating rating) {
	rating.setRatingId(ratingId);
	logger.log(Level.INFO, "update, rating: " + rating);
	ReturnMessageType result = ratingService.updateRating(rating);
	// if (ReturnMessageType.success.equals(result)) {
	// userService.clearSession();
	// }
	return result;
    }
}