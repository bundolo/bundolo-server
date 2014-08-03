package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.dao.ConnectionDAO;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.enumeration.ConnectionKindType;
import org.bundolo.model.enumeration.ConnectionStatusType;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
	    connection.setConnectionStatus(ConnectionStatusType.active);
	    connection.setCreationDate(new Date());
	    connection.setKind(ConnectionKindType.general);

	    Content descriptionContent = connection.getDescriptionContent();
	    descriptionContent.setAuthorUsername(connection.getAuthorUsername());
	    descriptionContent.setContentStatus(ContentStatusType.active);
	    descriptionContent.setCreationDate(connection.getCreationDate());
	    descriptionContent.setKind(ContentKindType.connection_description);
	    descriptionContent.setLocale(Constants.DEFAULT_LOCALE);
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
    public Connection findConnection(String title) {
	return connectionDAO.findByTitle(title);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean saveOrUpdateConnection(Connection connection) {
	try {
	    if (connection == null || connection.getDescriptionContent() == null
		    || StringUtils.isBlank(connection.getDescriptionContent().getName())) {
		return false;
	    }
	    UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
		    .getContext().getAuthentication();
	    if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
		// TODO validate connection group id
		if (connection.getConnectionId() == null) {
		    connection.setAuthorUsername((String) authentication.getPrincipal());
		    return saveConnection(connection);
		} else {
		    Connection connectionDB = connectionDAO.findById(connection.getConnectionId());
		    if (connectionDB == null) {
			// no such connection
			return false;
		    } else {
			if (!((String) authentication.getPrincipal()).equals(connectionDB.getAuthorUsername())) {
			    // user is not the owner
			    return false;
			}
			Content descriptionContent = connection.getDescriptionContent();
			Content descriptionContentDB = connectionDB.getDescriptionContent();
			descriptionContentDB.setName(descriptionContent.getName());
			descriptionContentDB.setText(descriptionContent.getText());
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

}
