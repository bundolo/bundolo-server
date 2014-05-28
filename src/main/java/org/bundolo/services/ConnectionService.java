package org.bundolo.services;

import java.util.List;

import org.bundolo.model.Connection;

public interface ConnectionService {

    public Connection findConnection(Long connectionId);

    public Long saveConnection(Connection connection) throws Exception;

    public void updateConnection(Connection connection) throws Exception;

    public void deleteConnection(Long connectionId) throws Exception;

    public List<Connection> findItemListConnections(String query, Integer start, Integer end) throws Exception;

    public Integer findItemListConnectionsCount(String query) throws Exception;
}
