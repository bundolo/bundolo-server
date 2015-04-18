package org.bundolo;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
@ImportResource("/applicationContext.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class MailingUtilsTest {

    @Autowired
    private MailingUtils mailingUtils;

    @Autowired
    @Qualifier("properties")
    private Properties properties;

    @Test
    public void newsletterSenderTest() {
	mailingUtils.newsletterSender();

	// check db state
	assertEquals("Filtering returned unexpected number of results", 1, 1);
    }

}