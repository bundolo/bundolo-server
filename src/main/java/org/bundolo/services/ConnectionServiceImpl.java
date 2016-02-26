package org.bundolo.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.DateUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.ConnectionDAO;
import org.bundolo.dao.ContentDAO;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ConnectionKindType;
import org.bundolo.model.enumeration.ConnectionStatusType;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("connectionService")
public class ConnectionServiceImpl implements ConnectionService {

    private static final Logger logger = Logger.getLogger(ConnectionServiceImpl.class.getName());

    @Autowired
    private ConnectionDAO connectionDAO;

    @Autowired
    private ContentDAO contentDAO;

    @Autowired
    private DateUtils dateUtils;

    @PostConstruct
    public void init() throws Exception {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    public Connection findConnection(Long connectionId) {
	Connection result = connectionDAO.findById(connectionId);
	return result;
	// return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private ResponseEntity<String> saveConnection(Connection connection) {
	try {
	    if (connectionDAO.findByTitle(connection.getDescriptionContent().getName()) != null) {
		// connection title already taken
		return new ResponseEntity<String>(ReturnMessageType.title_taken.name(), HttpStatus.BAD_REQUEST);
	    }
	    connection.setConnectionStatus(ConnectionStatusType.active);
	    connection.setCreationDate(dateUtils.newDate());
	    connection.setKind(ConnectionKindType.general);

	    Content descriptionContent = connection.getDescriptionContent();
	    descriptionContent.setAuthorUsername(connection.getAuthorUsername());
	    descriptionContent.setContentStatus(ContentStatusType.active);
	    descriptionContent.setCreationDate(connection.getCreationDate());
	    descriptionContent.setKind(ContentKindType.connection_description);
	    descriptionContent.setLocale(Constants.DEFAULT_LOCALE_NAME);
	    descriptionContent.setLastActivity(dateUtils.newDate());
	    descriptionContent.setSlug(contentDAO.getNewSlug(descriptionContent));
	    connectionDAO.persist(connection);
	    return new ResponseEntity<String>(descriptionContent.getSlug(), HttpStatus.OK);
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveConnection exception: " + ex);
	    return new ResponseEntity<String>(ReturnMessageType.exception.name(), HttpStatus.BAD_REQUEST);
	}
    }

    // @Override
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // public void deleteConnection(Long connectionId) throws Exception {
    // Connection connection = connectionDAO.findById(connectionId);
    // if (connection != null) {
    // connectionDAO.remove(connection);
    // }
    // }

    @Override
    public List<Connection> findConnections(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	return connectionDAO.findConnections(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Connection findConnection(String slug) {
	Connection connection = connectionDAO.findBySlug(slug);
	if (connection != null) {
	    Collection<Rating> ratings = connection.getDescriptionContent().getRating();
	    if (ratings == null) {
		ratings = new ArrayList<Rating>();
		connection.getDescriptionContent().setRating(ratings);
	    }
	    Rating rating = connection.getDescriptionContent().getRating().size() > 0 ? (Rating) connection
		    .getDescriptionContent().getRating().toArray()[0] : null;
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = connection.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !connection.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? dateUtils
		    .newDate() : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, 0l, connection.getDescriptionContent());
		connection.getDescriptionContent().getRating().add(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    connectionDAO.merge(connection);
	}
	return connection;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseEntity<String> saveOrUpdateConnection(Connection connection) {
	try {
	    if (connection == null || connection.getDescriptionContent() == null
		    || StringUtils.isBlank(connection.getDescriptionContent().getName())) {
		return new ResponseEntity<String>(ReturnMessageType.no_data.name(), HttpStatus.BAD_REQUEST);
	    }
	    String senderUsername = SecurityUtils.getUsername();
	    if (senderUsername != null) {
		// TODO validate connection group id
		if (connection.getConnectionId() == null) {
		    connection.setAuthorUsername(senderUsername);
		    return saveConnection(connection);
		} else {
		    Connection connectionDB = connectionDAO.findById(connection.getConnectionId());
		    if (connectionDB == null) {
			// no such connection
			return new ResponseEntity<String>(ReturnMessageType.not_found.name(), HttpStatus.BAD_REQUEST);
		    } else {
			if (!senderUsername.equals(connectionDB.getAuthorUsername())) {
			    // user is not the owner
			    return new ResponseEntity<String>(ReturnMessageType.not_owner.name(),
				    HttpStatus.BAD_REQUEST);
			}
			Content descriptionContent = connection.getDescriptionContent();
			Content descriptionContentDB = connectionDB.getDescriptionContent();
			if (!descriptionContentDB.getName().equals(descriptionContent.getName())) {
			    if (connectionDAO.findByTitle(descriptionContent.getName()) != null) {
				return new ResponseEntity<String>(ReturnMessageType.title_taken.name(),
					HttpStatus.BAD_REQUEST);
			    }
			    descriptionContentDB.setName(descriptionContent.getName());
			    descriptionContentDB.setSlug(contentDAO.getNewSlug(descriptionContentDB));
			}
			descriptionContentDB.setText(descriptionContent.getText());
			descriptionContentDB.setLastActivity(dateUtils.newDate());
			connectionDB.setEmail(connection.getEmail());
			connectionDB.setUrl(connection.getUrl());
			connectionDAO.merge(connectionDB);
			return new ResponseEntity<String>(descriptionContentDB.getSlug(), HttpStatus.OK);
		    }
		}
	    } else {
		return new ResponseEntity<String>(ReturnMessageType.anonymous_not_allowed.name(),
			HttpStatus.BAD_REQUEST);
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveOrUpdateConnection exception: " + ex);
	    return new ResponseEntity<String>(ReturnMessageType.exception.name(), HttpStatus.BAD_REQUEST);
	}
    }

    @Override
    public void clearSession() {
	connectionDAO.clear();
    }

    @Override
    public Connection findNext(Long connectionId, String orderBy, String fixBy, boolean ascending) {
	return connectionDAO.findNext(connectionId, orderBy, fixBy, ascending);
    }

    @Override
    public Long deleteConnection(String slug) {
	// TODO
	return null;
    }

}
