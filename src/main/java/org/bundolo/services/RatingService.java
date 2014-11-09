package org.bundolo.services;

import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ReturnMessageType;

public interface RatingService {

    public Rating findPersonalRating(Long contentId);

    public ReturnMessageType updateRating(Rating rating);

}
