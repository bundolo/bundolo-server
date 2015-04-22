package org.bundolo;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Properties;

import org.bundolo.dao.UserProfileDAO;
import org.bundolo.model.UserProfile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Before
    public void setUp() {
	userProfileDAO.resetSubscribers();
	// LogManager.getLogManager().reset();
	// Logger.getLogger("").addHandler(new ConsoleHandler());
    }

    @Test
    public void newsletterSenderIdleTest() {
	Calendar nowTime = Calendar.getInstance();
	Calendar startTime = Calendar.getInstance();
	startTime.set(2015, Calendar.APRIL, 12, 1, 0, 0);
	Long offset = startTime.getTimeInMillis() - nowTime.getTimeInMillis();
	properties.setProperty("date.offset", offset.toString());

	long dailyRecipientsBefore = userProfileDAO.dailyRecipientsCount(startTime.getTime());

	mailingUtils.newsletterSender();
	long dailyRecipientsAfter = userProfileDAO.dailyRecipientsCount(startTime.getTime());

	assertEquals("no emails should be sent if newsletter date has not been reached yet", dailyRecipientsBefore,
		dailyRecipientsAfter);
    }

    @Test
    public void newsletterSenderSendingTest() {
	Calendar nowTime = Calendar.getInstance();
	Calendar startTime = Calendar.getInstance();
	startTime.set(2015, Calendar.APRIL, 13, 1, 0, 0);
	Long offset = startTime.getTimeInMillis() - nowTime.getTimeInMillis();
	properties.setProperty("date.offset", offset.toString());

	long dailyRecipientsBefore = userProfileDAO.dailyRecipientsCount(startTime.getTime());

	mailingUtils.newsletterSender();
	long dailyRecipientsAfter = userProfileDAO.dailyRecipientsCount(startTime.getTime());

	assertEquals("unexpected number of sent emails",
		Integer.parseInt(properties.getProperty("newsletter.batch.size")), dailyRecipientsAfter
			- dailyRecipientsBefore);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void newsletterSenderUndeliverablesTest() {
	Calendar nowTime = Calendar.getInstance();
	Calendar startTime = Calendar.getInstance();
	startTime.set(2015, Calendar.APRIL, 12, 0, 0, 0);
	Long offset = startTime.getTimeInMillis() - nowTime.getTimeInMillis();
	properties.setProperty("date.offset", offset.toString());

	// simulate one undeliverable
	UserProfile user = userProfileDAO.findByField("email", "daniel.farkas0@gmail.com");
	user.setNewsletterSendingDate(null);
	userProfileDAO.merge(user);

	long dailyUndeliverablesBefore = userProfileDAO.dailyUndeliverablesCount();

	mailingUtils.newsletterSender();
	long dailyUndeliverablesAfter = userProfileDAO.dailyUndeliverablesCount();

	assertEquals("unexpected number of undeliverables", dailyUndeliverablesBefore - 1, dailyUndeliverablesAfter);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void newsletterSenderSendingSingleTest() {
	Calendar nowTime = Calendar.getInstance();
	Calendar startTime = Calendar.getInstance();
	startTime.set(2015, Calendar.APRIL, 13, 1, 0, 0);
	Long offset = startTime.getTimeInMillis() - nowTime.getTimeInMillis();
	properties.setProperty("date.offset", offset.toString());

	// simulate one subscriber
	userProfileDAO.unsubscribeAll();
	UserProfile user = userProfileDAO.findByField("email", "daniel.farkas0@gmail.com");
	user.setSubscribed(true);
	userProfileDAO.merge(user);

	long dailyRecipientsBefore = userProfileDAO.dailyRecipientsCount(startTime.getTime());

	mailingUtils.newsletterSender();
	long dailyRecipientsAfter = userProfileDAO.dailyRecipientsCount(startTime.getTime());

	assertEquals("unexpected number of sent emails", 1, dailyRecipientsAfter - dailyRecipientsBefore);
    }

}