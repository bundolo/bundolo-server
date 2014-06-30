package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Connection;

public interface ConnectionService {

    public Connection findConnection(Long connectionId);

    public Connection findConnection(String title);

    public Long saveConnection(Connection connection) throws Exception;

    public void updateConnection(Connection connection) throws Exception;

    public void deleteConnection(Long connectionId) throws Exception;

    public List<Connection> findConnections(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter);
}
