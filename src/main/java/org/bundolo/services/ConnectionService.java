package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Connection;

public interface ConnectionService {

    public Connection findConnection(Long connectionId);

    public Connection findConnection(String title);

    // public void deleteConnection(Long connectionId);

    public Boolean saveOrUpdateConnection(Connection connection);

    public List<Connection> findConnections(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);

    public Connection findNext(Long connectionId, String orderBy, String fixBy, boolean ascending);

    public void clearSession();
}
