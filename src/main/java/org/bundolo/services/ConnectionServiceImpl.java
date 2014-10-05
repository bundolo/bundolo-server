package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.ConnectionDAO;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.enumeration.ConnectionKindType;
import org.bundolo.model.enumeration.ConnectionStatusType;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("connectionService")
public class ConnectionServiceImpl implements ConnectionService {

    private static final Logger logger = Logger.getLogger(ConnectionServiceImpl.class.getName());

    @Autowired
    private ConnectionDAO connectionDAO;

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
    private Boolean saveConnection(Connection connection) {
	try {
	    if (connectionDAO.findByTitle(connection.getDescriptionContent().getName()) != null) {
		// connection title already taken
		return false;
	    }
	    connection.setConnectionStatus(ConnectionStatusType.active);
	    connection.setCreationDate(new Date());
	    connection.setKind(ConnectionKindType.general);

	    Content descriptionContent = connection.getDescriptionContent();
	    descriptionContent.setAuthorUsername(connection.getAuthorUsername());
	    descriptionContent.setContentStatus(ContentStatusType.active);
	    descriptionContent.setCreationDate(connection.getCreationDate());
	    descriptionContent.setKind(ContentKindType.connection_description);
	    descriptionContent.setLocale(Constants.DEFAULT_LOCALE);
	    descriptionContent.setLastActivity(new Date());
	    connectionDAO.persist(connection);
	    return true;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveConnection exception: " + ex);
	}
	return false;
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
    public Connection findConnection(String title) {
	Connection connection = connectionDAO.findByTitle(title);
	if (connection != null) {
	    Rating rating = connection.getDescriptionContent().getRating();
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = connection.getAuthorUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !connection.getAuthorUsername().equals(SecurityUtils.getUsername()) || rating == null ? new Date()
		    : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, connection.getDescriptionContent());
		connection.getDescriptionContent().setRating(rating);
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
    public Boolean saveOrUpdateConnection(Connection connection) {
	try {
	    if (connection == null || connection.getDescriptionContent() == null
		    || StringUtils.isBlank(connection.getDescriptionContent().getName())) {
		return false;
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
			return false;
		    } else {
			if (!senderUsername.equals(connectionDB.getAuthorUsername())) {
			    // user is not the owner
			    return false;
			}
			Content descriptionContent = connection.getDescriptionContent();
			Content descriptionContentDB = connectionDB.getDescriptionContent();
			if (!descriptionContentDB.getName().equals(descriptionContent.getName())
				&& connectionDAO.findByTitle(descriptionContent.getName()) != null) {
			    // new connection title already taken
			    return false;
			}
			descriptionContentDB.setName(descriptionContent.getName());
			descriptionContentDB.setText(descriptionContent.getText());
			descriptionContentDB.setLastActivity(new Date());
			connectionDB.setEmail(connection.getEmail());
			connectionDB.setUrl(connection.getUrl());
			connectionDAO.merge(connectionDB);
			return true;
		    }
		}
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveOrUpdateConnection exception: " + ex);
	}
	return false;
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
    public Long deleteConnection(String title) {
	// TODO
	return null;
    }

}
