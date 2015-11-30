package org.bundolo;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.services.ContentService;
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
public class PopulateSlugsTest {

    private static final Logger logger = Logger.getLogger(PopulateSlugsTest.class.getName());

    @Autowired
    private ContentService contentService;

    @Test
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void populateSlugs() {
	int pageCounter = 0;
	while (contentService.populateSlugs(pageCounter) == Constants.PAGE_SIZE) {
	    pageCounter++;
	    logger.log(Level.WARNING, "progress: " + (Constants.PAGE_SIZE * pageCounter));
	    // contentService.clearSession();
	}
	// contentService.clearSession();
    }

}