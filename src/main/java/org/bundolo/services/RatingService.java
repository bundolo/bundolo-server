package org.bundolo.services;

import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.springframework.http.ResponseEntity;

public interface RatingService {

    public Rating findPersonalRating(Long contentId);

    public ReturnMessageType updateRating(Rating rating);

    public ResponseEntity<String> resetHistoricalRatings(String slug);

}