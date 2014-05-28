package org.bundolo.dao;

import java.util.logging.Logger;

import org.bundolo.model.Rating;
import org.springframework.stereotype.Repository;

@Repository("ratingDAO")
public class RatingDAO extends JpaDAO<Long, Rating> {

    private static final Logger logger = Logger.getLogger(RatingDAO.class.getName());

}