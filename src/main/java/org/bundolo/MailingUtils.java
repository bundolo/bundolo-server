package org.bundolo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.dao.UserProfileDAO;
import org.bundolo.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class MailingUtils {

    private static final Logger logger = Logger.getLogger(MailingUtils.class.getName());

    @Autowired
    @Qualifier("properties")
    private Properties properties;

    @Autowired
    private Configuration freemarkerConfiguration;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private DateUtils dateUtils;

    public void sendEmail(String body, String subject, String recipient) throws MessagingException,
	    UnsupportedEncodingException {
	if (Boolean.valueOf(properties.getProperty("mailing.active"))) {
	    // logger.log(Level.INFO, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject + "\nbody: " +
	    // body);
	    logger.log(Level.INFO, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject);
	    if ("gmail".equals(properties.getProperty("mail.service"))) {
		sendEmailGmail(body, subject, recipient);
	    } else if ("godaddy".equals(properties.getProperty("mail.service"))) {
		sendEmailGoDaddy(body, subject, recipient);
	    }
	} else {
//	    logger.log(Level.WARNING, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject + "\nbody: "
//		    + body);
	    logger.log(Level.WARNING, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject);
	}
    }

    private void sendEmailGmail(String body, String subject, String recipient) throws MessagingException,
	    UnsupportedEncodingException {
	Properties mailProps = new Properties();
	mailProps.put("mail.smtp.from", properties.getProperty("mail.from"));
	mailProps.put("mail.smtp.host", properties.getProperty("mail.host"));
	mailProps.put("mail.smtp.port", properties.getProperty("mail.port"));
	mailProps.put("mail.smtp.auth", "true");
	mailProps.put("mail.smtp.socketFactory.port", properties.getProperty("mail.port"));
	mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	mailProps.put("mail.smtp.socketFactory.fallback", "false");
	mailProps.put("mail.smtp.starttls.enable", "true");

	Session mailSession = Session.getDefaultInstance(mailProps, new Authenticator() {
	    @Override
	    protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(properties.getProperty("mail.username"), properties
			.getProperty("mail.password"));
	    }
	});

	MimeMessage message = new MimeMessage(mailSession);
	message.setFrom(new InternetAddress(properties.getProperty("mail.from"), properties
		.getProperty("mail.from.friendly")));
	String[] emails = { recipient };
	InternetAddress dests[] = new InternetAddress[emails.length];
	for (int i = 0; i < emails.length; i++) {
	    dests[i] = new InternetAddress(emails[i].trim().toLowerCase());
	}
	message.setRecipients(Message.RecipientType.TO, dests);
	message.setSubject(subject, "UTF-8");
	Multipart mp = new MimeMultipart();
	MimeBodyPart mbp = new MimeBodyPart();
	mbp.setContent(body, "text/html;charset=utf-8");
	mp.addBodyPart(mbp);
	message.setContent(mp);
	message.setSentDate(new java.util.Date());
	Transport.send(message);
    }

    private void sendEmailGoDaddy(String body, String subject, String recipient) throws MessagingException,
	    UnsupportedEncodingException {
	Properties props = new Properties();
	props.put("mail.transport.protocol", "smtps");
	props.put("mail.smtps.host", properties.getProperty("mail.host"));
	props.put("mail.smtps.auth", "true");

	Session mailSession = Session.getDefaultInstance(props);
	// mailSession.setDebug(true);
	Transport transport = mailSession.getTransport();
	MimeMessage message = new MimeMessage(mailSession);

	message.setSubject(subject, "UTF-8");
	message.setContent(body, "text/plain;charset=utf-8");
	message.setFrom(new InternetAddress(properties.getProperty("mail.from"), properties
		.getProperty("mail.from.friendly")));
	message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
	transport.connect(properties.getProperty("mail.host"), Integer.valueOf(properties.getProperty("mail.port")),
		properties.getProperty("mail.username"), properties.getProperty("mail.password"));
	transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
	transport.close();
    }

    @Scheduled(cron = "${systemProperties['newsletter.sender.schedule'] ?: 0 0 * * * *}")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void newsletterSender() {
	Calendar now = dateUtils.newCalendar();
	logger.log(Level.WARNING, "newsletterSender " + now.getTime());

	// update undeliverables if it's a next day
	if (now.get(Calendar.HOUR_OF_DAY) == 0) {
	    userProfileDAO.unsubscribeUndeliverables();
	}

	String newsletterDate = properties.getProperty("newsletter.date");
	String newsletterBatchSize = properties.getProperty("newsletter.batch.size");
	String newsletterDailyMax = properties.getProperty("newsletter.daily.recipients");
	String newsletterUndeliverablesMax = properties.getProperty("newsletter.daily.undeliverables");
	if (StringUtils.isNotBlank(newsletterDate) && StringUtils.isNotBlank(newsletterBatchSize)
		&& StringUtils.isNotBlank(newsletterDailyMax) && StringUtils.isNotBlank(newsletterUndeliverablesMax)) {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	    Date sendingStart;
	    try {
		sendingStart = formatter.parse(newsletterDate);
		int batchSize = Integer.parseInt(newsletterBatchSize);
		int dailyMax = Integer.parseInt(newsletterDailyMax);
		int undeliverablesMax = Integer.parseInt(newsletterUndeliverablesMax);

		// check has time for newsletter sending arrived
		// check have we reached max daily recipients
		// check have we reached max daily undeliverables
		long dailyRecipientsCount = userProfileDAO.dailyRecipientsCount(now.getTime());
		long dailyUndeliverablesCount = userProfileDAO.dailyUndeliverablesCount();
		if ((now.getTime().after(sendingStart)) && (dailyRecipientsCount < dailyMax)
			&& (dailyUndeliverablesCount < undeliverablesMax)) {
		    List<UserProfile> recipients = userProfileDAO.findNewsletterUsers(sendingStart, batchSize);
		    if (recipients != null && recipients.size() > 0) {

			Template bodyTemplate;
			Template subjectTemplate;
			try {
			    bodyTemplate = freemarkerConfiguration.getTemplate("newsletter_" + newsletterDate + ".ftl");
			    subjectTemplate = freemarkerConfiguration.getTemplate("newsletter_subject_"
				    + newsletterDate + ".ftl");
			} catch (IOException ex) {
			    logger.log(Level.SEVERE, "newsletter template retrieval exception: " + ex);
			    return;
			}
			for (UserProfile recipient : recipients) {
			    if ((recipient != null) && (dailyRecipientsCount < dailyMax)
				    && (dailyUndeliverablesCount < undeliverablesMax)) {
				try {
				    Map<String, Object> data = new HashMap<String, Object>();
				    data.put("recipient", recipient);
				    String newsletterBody = FreeMarkerTemplateUtils.processTemplateIntoString(
					    bodyTemplate, data);
				    String newsletterSubject = FreeMarkerTemplateUtils.processTemplateIntoString(
					    subjectTemplate, data);
				    sendEmail(newsletterBody, newsletterSubject, recipient.getEmail());
				    dailyRecipientsCount++;
				    recipient.setNewsletterSendingDate(now.getTime());
				    userProfileDAO.merge(recipient);
				} catch (Exception ex) {
				    logger.log(Level.WARNING, "newsletter undeliverable to: " + recipient.getUsername());
				    dailyUndeliverablesCount++;
				    recipient.setNewsletterSendingDate(null);
				    userProfileDAO.merge(recipient);
				}
			    }
			}
		    }
		}
	    } catch (ParseException e) {
		logger.log(Level.SEVERE, "newsletterSender currentNewsletter date format exception: " + newsletterDate);
		return;
	    }
	}
    }

    private static TriggerContext getTriggerContext(Date lastCompletionTime) {
	SimpleTriggerContext context = new SimpleTriggerContext();
	context.update(null, null, lastCompletionTime);
	return context;
    }
}
