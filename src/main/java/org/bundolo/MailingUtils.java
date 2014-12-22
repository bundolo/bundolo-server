package org.bundolo;

import java.io.UnsupportedEncodingException;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MailingUtils {

    private static final Logger logger = Logger.getLogger(MailingUtils.class.getName());

    @Autowired
    @Qualifier("properties")
    private Properties properties;

    public void sendEmail(String body, String subject, String recipient) throws MessagingException,
	    UnsupportedEncodingException {
	if (Boolean.valueOf(properties.getProperty("mailing.active"))) {
	    logger.log(Level.INFO, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject + "\nbody: " + body);
	    if ("gmail".equals(properties.getProperty("mail.service"))) {
		sendEmailGmail(body, subject, recipient);
	    } else if ("godaddy".equals(properties.getProperty("mail.service"))) {
		sendEmailGoDaddy(body, subject, recipient);
	    }
	} else {
	    logger.log(Level.WARNING, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject + "\nbody: "
		    + body);
	}
    }

    private void sendEmailGmail(String body, String subject, String recipient) throws MessagingException,
	    UnsupportedEncodingException {
	Properties mailProps = new Properties();
	mailProps.put("mail.smtp.from", properties.getProperty("mail.from"));
	mailProps.put("mail.smtp.host", properties.getProperty("mail.host"));
	mailProps.put("mail.smtp.port", properties.getProperty("mail.port"));
	mailProps.put("mail.smtp.auth", true);
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
	message.setFrom(new InternetAddress(properties.getProperty("mail.from")));
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

    public static String format(String s, Object... arguments) {
	// A very simple implementation of format
	int i = 0;
	while (i < arguments.length) {
	    String delimiter = "{" + i + "}";
	    while (s.contains(delimiter)) {
		s = s.replace(delimiter, String.valueOf(arguments[i]));
	    }
	    i++;
	}
	return s;
    }
}
