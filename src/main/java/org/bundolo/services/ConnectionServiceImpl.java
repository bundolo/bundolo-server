package org.bundolo.services;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.dao.ConnectionDAO;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("connectionService")
public class ConnectionServiceImpl implements ConnectionService {

    private static final Logger logger = Logger.getLogger(ConnectionServiceImpl.class.getName());

    @Autowired
    private ConnectionDAO connectionDAO;

    @Autowired
    private ContentService contentService;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long saveConnection(Connection connection) throws Exception {
	Long result = null;
	Connection connectionDB = null;
	if (connection.getConnectionId() != null) {
	    connectionDB = connectionDAO.findById(connection.getConnectionId());
	}
	if (connectionDB == null) {
	    // if (connection.getDescriptionContent() != null) {
	    // Long contentId = contentService.saveContent(connection.getDescriptionContent());
	    // connectionDB = new Connection(connection.getConnectionId(), connection.getAuthorUsername(),
	    // connection.getParentContentId(), contentId, connection.getKind(), new Date(),
	    // connection.getConnectionStatus(), connection.getEmail(), connection.getUrl());
	    try {
		connectionDAO.persist(connection);
	    } catch (Exception ex) {
		// contentService.deleteContent(contentId);
		throw new Exception("db exception");
	    }
	    result = connection.getConnectionId();
	    // }
	}
	return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateConnection(Connection connection) throws Exception {
	Connection connectionDB = connectionDAO.findById(connection.getConnectionId());

	if (connectionDB != null) {
	    Content descriptionContent = connection.getDescriptionContent();
	    if (descriptionContent != null) {
		if (descriptionContent.getContentId() == null) {
		    contentService.saveContent(connection.getDescriptionContent());
		} else {
		    contentService.updateContent(connection.getDescriptionContent());
		}
	    }
	    connectionDB.setConnectionStatus(connection.getConnectionStatus());
	    connectionDB.setCreationDate(connection.getCreationDate());
	    connectionDB.setEmail(connection.getEmail());
	    connectionDB.setKind(connection.getKind());
	    connectionDB.setUrl(connection.getUrl());
	    connectionDAO.merge(connectionDB);
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteConnection(Long connectionId) throws Exception {
	Connection connection = connectionDAO.findById(connectionId);
	if (connection != null) {
	    connectionDAO.remove(connection);
	}
    }

    @Override
    public List<Connection> findConnections(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	return connectionDAO.findConnections(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    public Connection findConnection(String title) {
	return connectionDAO.findByTitle(title);
    }

}
