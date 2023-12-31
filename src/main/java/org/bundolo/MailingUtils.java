package org.bundolo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
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

import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.NewsletterSubscriptionKindType;
import org.bundolo.services.ContentService;
import org.bundolo.services.RatingService;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
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
	private UserService userService;

	@Autowired
	private DateUtils dateUtils;

	@Autowired
	private ContentService contentService;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private MessageSource messages;

	Template bodyTemplateBulletin;
	Template subjectTemplateBulletin;
	Template bodyTemplateDigest;
	Template subjectTemplateDigest;

	public void sendEmail(String body, String subject, String recipient)
			throws MessagingException, UnsupportedEncodingException {
		if (Boolean.valueOf(properties.getProperty("mailing.active"))) {
			// logger.log(Level.INFO, "sendEmail\nrecipient: " + recipient +
			// "\nsubject: " + subject + "\nbody: " +
			// body);
			logger.log(Level.INFO, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject);
			if ("gmail".equals(properties.getProperty("mail.service"))) {
				sendEmailGmail(body, subject, recipient);
			} else if ("godaddy".equals(properties.getProperty("mail.service"))) {
				sendEmailGoDaddy(body, subject, recipient);
			} else if ("bundolo".equals(properties.getProperty("mail.service"))) {
				sendEmailBundolo(body, subject, recipient);
			} else if ("amazon".equals(properties.getProperty("mail.service"))) {
				sendEmailAmazon(body, subject, recipient);
			}
		} else {
			// logger.log(Level.WARNING,
			// "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject +
			// "\nbody: " + body);
			logger.log(Level.WARNING, "sendEmail\nrecipient: " + recipient + "\nsubject: " + subject);
		}
	}

	private void sendEmailGmail(String body, String subject, String recipient)
			throws MessagingException, UnsupportedEncodingException {
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
				return new PasswordAuthentication(properties.getProperty("mail.username"),
						properties.getProperty("mail.password"));
			}
		});

		MimeMessage message = new MimeMessage(mailSession);
		message.setFrom(
				new InternetAddress(properties.getProperty("mail.from"), properties.getProperty("mail.from.friendly")));
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

	private void sendEmailGoDaddy(String body, String subject, String recipient)
			throws MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", properties.getProperty("mail.host"));
		props.put("mail.smtps.auth", "true");

		Session mailSession = Session.getDefaultInstance(props);
		// mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();
		MimeMessage message = new MimeMessage(mailSession);

		message.setSubject(subject, "UTF-8");
		message.setContent(body, "text/html;charset=utf-8");
		message.setFrom(
				new InternetAddress(properties.getProperty("mail.from"), properties.getProperty("mail.from.friendly")));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		transport.connect(properties.getProperty("mail.host"), Integer.valueOf(properties.getProperty("mail.port")),
				properties.getProperty("mail.username"), properties.getProperty("mail.password"));
		transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	private void sendEmailBundolo(String body, String subject, String recipient)
			throws MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", properties.getProperty("mail.host"));

		Session mailSession = Session.getDefaultInstance(props);
		// mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();
		MimeMessage message = new MimeMessage(mailSession);

		message.setSubject(subject, "UTF-8");
		message.setContent(body, "text/html;charset=utf-8");
		message.setFrom(
				new InternetAddress(properties.getProperty("mail.from"), properties.getProperty("mail.from.friendly")));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		transport.connect(properties.getProperty("mail.host"), Integer.valueOf(properties.getProperty("mail.port")),
				properties.getProperty("mail.username"), properties.getProperty("mail.password"));
		transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	private void sendEmailAmazon(String body, String subject, String recipient)
			throws MessagingException, UnsupportedEncodingException {
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtps");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.port", properties.getProperty("mail.port"));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		Session mailSession = Session.getDefaultInstance(props);
		//session.setDebug(true);

		MimeMessage message = new MimeMessage(mailSession);
		message.setFrom(
				new InternetAddress(properties.getProperty("mail.from"), properties.getProperty("mail.from.friendly")));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		message.setSubject(subject, "UTF-8");
		message.setContent(body, "text/html;charset=utf-8");

		Transport transport = mailSession.getTransport();
		transport.connect(properties.getProperty("mail.host"), properties.getProperty("mail.username"),
				properties.getProperty("mail.password"));
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	@Scheduled(cron = "${systemProperties['newsletter.sender.schedule'] ?: 0 0/10 * * * *}")
	// every 10 minutes 50 users means 50x6x24=7200 users per day
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void newsletterSender() {
		try {
			Calendar now = dateUtils.newCalendar();
			logger.log(Level.INFO, "newsletterSender " + now.getTime());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date bulletinDate = formatter.parse(properties.getProperty("bulletin.date"));

			List<UserProfile> recipients = userService.findNewsletterUsers(now.getTime(), bulletinDate, 50);
			if (recipients != null && recipients.size() > 0) {
				for (UserProfile recipient : recipients) {
					List<NewsletterSubscriptionKindType> subscriptions = NewsletterSubscriptionKindType
							.getListRepresentation(recipient.getNewsletterSubscriptions());
					if (subscriptions != null) {
						if (subscriptions.contains(NewsletterSubscriptionKindType.bulletin)
								&& recipient.getNewsletterSendingDate().before(bulletinDate)
								&& bulletinDate.before(now.getTime())) {
							sendBulletin(recipient);
						}
						if (subscriptions.contains(NewsletterSubscriptionKindType.daily) && DateUtils
								.getDateDiff(recipient.getNewsletterSendingDate(), now.getTime(), TimeUnit.DAYS) >= 1) {
							sendDigest(now, recipient, NewsletterSubscriptionKindType.daily);
						}
						if (subscriptions.contains(NewsletterSubscriptionKindType.weekly) && DateUtils
								.getDateDiff(recipient.getNewsletterSendingDate(), now.getTime(), TimeUnit.DAYS) >= 7) {
							sendDigest(now, recipient, NewsletterSubscriptionKindType.weekly);
						}
						if (subscriptions.contains(NewsletterSubscriptionKindType.monthly)
								&& DateUtils.getDateDiff(recipient.getNewsletterSendingDate(), now.getTime(),
										TimeUnit.DAYS) >= 30) {
							sendDigest(now, recipient, NewsletterSubscriptionKindType.monthly);
						}
						Thread.sleep(3000);
					}
				}
				userService.updateNewsletterSendingDate(recipients, now.getTime());
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "newsletterSender exception: " + ex);
		}
	}

	private void sendBulletin(UserProfile recipient) {
		if (bodyTemplateBulletin == null || subjectTemplateBulletin == null) {
			try {
				bodyTemplateBulletin = freemarkerConfiguration
						.getTemplate("bulletin_" + properties.getProperty("bulletin.date") + ".ftl");
				subjectTemplateBulletin = freemarkerConfiguration
						.getTemplate("bulletin_subject_" + properties.getProperty("bulletin.date") + ".ftl");
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "newsletter template retrieval exception: " + ex);
				return;
			}
		}
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("recipient", recipient);
			String newsletterBody = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplateBulletin, data);
			String newsletterSubject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplateBulletin, data);
			sendEmail(newsletterBody, newsletterSubject, recipient.getEmail());
		} catch (Exception ex) {
			logger.log(Level.WARNING, "newsletter undeliverable to: " + recipient.getUsername());
			// recipient.setNewsletterSendingDate(null);
			// userProfileDAO.merge(recipient);
		}
	}

	private void sendDigest(Calendar sendingStart, UserProfile recipient, NewsletterSubscriptionKindType digestKind) {
		if (bodyTemplateDigest == null || subjectTemplateDigest == null) {
			try {
				bodyTemplateDigest = freemarkerConfiguration.getTemplate("digest.ftl");
				subjectTemplateDigest = freemarkerConfiguration.getTemplate("digest_subject.ftl");
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "digest template retrieval exception: " + ex);
				return;
			}
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		String formattedDate = dateFormat.format(sendingStart.getTime());

		StringBuilder updatesMessage = new StringBuilder("bilo je:");
		String periodMessage = "";
		String introMessage = "";
		Calendar from = (Calendar) sendingStart.clone();
		switch (digestKind) {
		case daily:
			from.add(Calendar.DATE, -1);
			periodMessage = "Dnevni";
			introMessage = "U prethodnom danu";
			break;
		case weekly:
			from.add(Calendar.DATE, -7);
			periodMessage = "Nedeljni";
			introMessage = "U prethodnoj nedelji";
			break;
		case monthly:
			from.add(Calendar.MONTH, -1);
			periodMessage = "Mesečni";
			introMessage = "U prethodnom mesecu";
			break;
		default:
			break;
		}
		// contentService.clearSession();
		// read recent for period
		List<Content> recentContents = contentService.findRecent(from.getTime(), 0);
		// trimming would affect further query results if session is not cleared
		contentService.clearSession();
		if (recentContents != null) {
			Map<ContentKindType, Long> newCounters = new EnumMap<ContentKindType, Long>(ContentKindType.class);
			Map<ContentKindType, Long> updatedCounters = new EnumMap<ContentKindType, Long>(ContentKindType.class);
			for (Content recentContent : recentContents) {
				// count all
				if (recentContent.getCreationDate().after(from.getTime())) {
					newCounters.put(recentContent.getKind(), zeroIfNull(newCounters.get(recentContent.getKind())) + 1);
				} else {
					updatedCounters.put(recentContent.getKind(),
							zeroIfNull(updatedCounters.get(recentContent.getKind())) + 1);
				}
			}
			for (Map.Entry<ContentKindType, Long> entry : newCounters.entrySet()) {
				ContentKindType key = entry.getKey();
				Long value = entry.getValue();
				if (value != null && value > 0) {
					updatesMessage.append("\n<br/> " + value + " novih "
							+ messages.getMessage(key.getLocalizedName(), null, Constants.DEFAULT_LOCALE));
				}
			}
			for (Map.Entry<ContentKindType, Long> entry : updatedCounters.entrySet()) {
				ContentKindType key = entry.getKey();
				Long value = entry.getValue();
				if (value != null && value > 0) {
					updatesMessage.append("\n<br/> " + value + " aktivnih "
							+ messages.getMessage(key.getLocalizedName(), null, Constants.DEFAULT_LOCALE));
				}
			}
		}
		if ("bilo je:".equals(updatesMessage.toString())) {
			updatesMessage = new StringBuilder("nije bilo novosti.");
		}
		try {
			StringBuilder interactionsMessage = new StringBuilder("imali ste:");
			// contentService.clearSession();
			// ratingService.clearSession();
			// read interactions
			List<Content> authorInteractions = contentService.findAuthorInteractions(
					recipient.getDescriptionContent().getSlug(), from.getTime(), 0, -1, null, null, null, null);
			contentService.clearSession();
			if (authorInteractions != null) {
				Map<ContentKindType, Long> newRatings = new EnumMap<ContentKindType, Long>(ContentKindType.class);
				Map<ContentKindType, Long> newComments = new EnumMap<ContentKindType, Long>(ContentKindType.class);
				for (Content authorInteraction : authorInteractions) {
					// count all
					if (authorInteraction.getRating().size() >= 2) {
						Rating personalRating = (Rating) authorInteraction.getRating().toArray()[1];
						newRatings.put(authorInteraction.getKind(),
								zeroIfNull(newRatings.get(authorInteraction.getKind()))
										+ zeroIfNull(personalRating.getHistorical()));
						newComments.put(authorInteraction.getKind(),
								zeroIfNull(newComments.get(authorInteraction.getKind()))
										+ zeroIfNull(personalRating.getValue()));
					}
				}
				for (Map.Entry<ContentKindType, Long> entry : newRatings.entrySet()) {
					ContentKindType key = entry.getKey();
					Long value = entry.getValue();
					if (value != null && value > 0) {
						interactionsMessage.append("\n<br/> " + value + " novih pregleda "
								+ messages.getMessage(key.getLocalizedName(), null, Constants.DEFAULT_LOCALE));
					}
				}
				for (Map.Entry<ContentKindType, Long> entry : newComments.entrySet()) {
					ContentKindType key = entry.getKey();
					Long value = entry.getValue();
					if (value != null && value > 0) {
						interactionsMessage.append("\n<br/> " + value + " novih odgovora ispod "
								+ messages.getMessage(key.getLocalizedName(), null, Constants.DEFAULT_LOCALE));
					}
				}
			}
			if ("imali ste:".equals(interactionsMessage.toString())) {
				interactionsMessage = new StringBuilder("niste imali interakcija.");
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("username", recipient.getUsername());
			data.put("period", periodMessage);
			data.put("date", formattedDate);
			data.put("intro", introMessage);
			data.put("updates", updatesMessage);
			data.put("interactions", interactionsMessage);
			String digestBody = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplateDigest, data);
			String digestSubject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplateDigest, data);
			sendEmail(digestBody, digestSubject, recipient.getEmail());
		} catch (Exception ex) {
			// TODO reconsider unsubscribing if sending failed. exception does
			// not have to be permanent.
			// recipient.setDigestSubscription(DigestKindType.none);
			// recipient.setNewsletterSendingDate(null);
			// userProfileDAO.merge(recipient);
		}
	}

	public void sendMessage(String title, String text, String senderUsername, String recipientEmailAddress)
			throws Exception {
		Template bodyTemplate;
		Template subjectTemplate;
		bodyTemplate = freemarkerConfiguration.getTemplate("message.ftl");
		subjectTemplate = freemarkerConfiguration.getTemplate("message_subject.ftl");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("username", senderUsername);
		data.put("title", title);
		data.put("text", text);
		String digestBody = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, data);
		String digestSubject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate, data);
		sendEmail(digestBody, digestSubject, recipientEmailAddress);
	}

	public void sendRecommendation(String title, String slug, String senderUsername, String recipientUsername,
			String recipientEmailAddress) throws Exception {
		Template bodyTemplate;
		Template subjectTemplate;
		bodyTemplate = freemarkerConfiguration.getTemplate("recommendation.ftl");
		subjectTemplate = freemarkerConfiguration.getTemplate("recommendation_subject.ftl");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sender", senderUsername);
		data.put("title", title);
		data.put("slug", slug);
		data.put("recipient", recipientUsername);
		String digestBody = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, data);
		String digestSubject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate, data);
		sendEmail(digestBody, digestSubject, recipientEmailAddress);
	}

	//TODO make sure this is not used and remove
	private static TriggerContext getTriggerContext(Date lastCompletionTime) {
		SimpleTriggerContext context = new SimpleTriggerContext();
		context.update(null, null, lastCompletionTime);
		return context;
	}

	private Long zeroIfNull(Long value) {
		if (value == null) {
			return 0L;
		} else {
			return value;
		}
	}
}
