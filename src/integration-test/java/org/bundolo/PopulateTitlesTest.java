package org.bundolo;

import java.util.List;

import org.bundolo.dao.ContentDAO;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.services.ConnectionService;
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
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class PopulateTitlesTest {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ContentDAO contentDAO;

    @Test
    public void convertTitles() {
	List<Connection> connections = connectionService.findConnections(0, 9, null, null, null, null);
	for (Connection connection : connections) {
	    Content content = connection.getDescriptionContent();
	    System.out.println(content.getName() + " = " + contentDAO.getNewSlug(content));
	}

	// assertEquals("Unexpected result", "aaa", contentName);

	// first implement creating slug on save and update
	// read content name, create slug, check if already exists. if it does, set counter to 1. append counter to the
	// end. check if slug exists, if it does, increase counter and repeat while it's unique. save new slug.

    }
}