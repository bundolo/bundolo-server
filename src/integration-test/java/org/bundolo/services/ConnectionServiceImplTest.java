package org.bundolo.services;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bundolo.model.Connection;
import org.bundolo.model.enumeration.ConnectionColumnType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
@ImportResource("/applicationContext.xml")
// @ContextHierarchy(value = { @ContextConfiguration })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ConnectionServiceImplTest {

    // @Autowired
    // private ApplicationContext applicationContext;

    @Autowired
    private ConnectionService connectionService;

    @Test
    public void findConnections() {
	// ConnectionService connectionService = (ConnectionService) applicationContext.getBean("connectionService");

	List<Connection> connections = connectionService.findConnections(0, 4, null, null, new String[] {
		ConnectionColumnType.url.getColumnName(), ConnectionColumnType.title.getColumnName() }, new String[] {
		".hr", "borg" });
	assertEquals("Filtering returned unexpected number of results", connections.size(), 1);
    }

}