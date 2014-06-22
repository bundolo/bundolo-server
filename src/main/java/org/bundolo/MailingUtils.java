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
	    logger.log(Level.FINE, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject + "\nbody: " + body);
	    if (Boolean.valueOf(properties.getProperty("mail.from"))) {
		Properties mailProps = new Properties();
		mailProps.put("mail.smtp.from", properties.getProperty("mail.from"));
		mailProps.put("mail.smtp.host", properties.getProperty("mail.smtphost"));
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
	} else {
	    logger.log(Level.WARNING, "#####sendEmail\nrecipient: " + recipient + "\nsubject: " + subject + "\nbody: "
		    + body);
	}
    }
}
