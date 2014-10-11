package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Connection;
import org.bundolo.model.enumeration.ReturnMessageType;

public interface ConnectionService {

    public Connection findConnection(Long connectionId);

    public Connection findConnection(String title);

    public ReturnMessageType saveOrUpdateConnection(Connection connection);

    public List<Connection> findConnections(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    public Connection findNext(Long connectionId, String orderBy, String fixBy, boolean ascending);

    public void clearSession();

    public Long deleteConnection(String title);
}
