package org.bundolo.services;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.DateUtils;
import org.bundolo.MailingUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.UserDAO;
import org.bundolo.dao.UserProfileDAO;
import org.bundolo.model.Content;
import org.bundolo.model.Rating;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.RatingKindType;
import org.bundolo.model.enumeration.RatingStatusType;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.bundolo.model.enumeration.UserProfileStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private MailingUtils mailingUtils;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    @Qualifier("properties")
    private Properties properties;

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User findUser(String username) {
	User user = userDAO.findById(username);
	if (user != null) {
	    Collection<Rating> ratings = user.getDescriptionContent().getRating();
	    if (ratings == null) {
		ratings = new ArrayList<Rating>();
		user.getDescriptionContent().setRating(ratings);
	    }
	    Rating rating = user.getDescriptionContent().getRating().size() > 0 ? (Rating) user.getDescriptionContent()
		    .getRating().toArray()[0] : null;
	    // if user that requested this is the author, do not increase rating
	    long ratingIncrement = user.getUsername().equals(SecurityUtils.getUsername()) ? 0
		    : Constants.DEFAULT_RATING_INCREMENT;
	    Date lastActivity = !user.getUsername().equals(SecurityUtils.getUsername()) || rating == null ? dateUtils
		    .newDate() : rating.getLastActivity();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, lastActivity, RatingStatusType.active,
			ratingIncrement, user.getDescriptionContent());
		user.getDescriptionContent().getRating().add(rating);
	    } else {
		rating.setValue(rating.getValue() + ratingIncrement);
		rating.setLastActivity(lastActivity);
	    }
	    userDAO.merge(user);
	}
	return user;
    }

    @Override
    public List<User> findUsers(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter) {
	return userDAO.findUsers(start, end, orderBy, order, filterBy, filter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMessageType authenticateUser(String username, String password) {
	logger.log(Level.INFO, "login: " + username + ", " + password);
	ReturnMessageType result = ReturnMessageType.login_failed;
	// we intentionally set default values to make the method run the same amount of time regardless of being
	// successful or not
	String proposedUsername;
	String proposedPassword;
	if (StringUtils.isBlank(username)) {
	    proposedUsername = Constants.DEFAULT_GUEST_USERNAME;
	} else {
	    proposedUsername = username;
	}
	if (StringUtils.isBlank(password)) {
	    proposedPassword = " ";
	} else {
	    proposedPassword = password;
	}
	try {
	    UserProfile userProfile = userProfileDAO.findByField("username", proposedUsername);
	    String dbSalt;
	    String dbPassword;
	    UserProfileStatusType dbStatus;
	    if (userProfile != null) {
		dbSalt = userProfile.getSalt();
		dbPassword = userProfile.getPassword();
		dbStatus = userProfile.getUserProfileStatus();
	    } else {
		// if user is not found it is ok to skip the rest since usernames are not secret
		return ReturnMessageType.login_failed;
	    }
	    userProfile.setPreviousActivity(userProfile.getDescriptionContent().getLastActivity());
	    // update guest account in case of failed login to make this run the same amount of time regardless of being
	    // successful or not
	    userProfile.setLastLoginDate(dateUtils.newDate());
	    userProfile.setLastIp(getRemoteHost());
	    userProfileDAO.merge(userProfile);
	    if (SecurityUtils.getHashWithPredefinedSalt(proposedPassword, dbSalt).equals(dbPassword)
		    && (UserProfileStatusType.active.equals(dbStatus))) {
		result = ReturnMessageType.success;
	    } else {
		result = ReturnMessageType.login_failed;
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "authenticateUser exception: " + ex);
	    result = ReturnMessageType.exception;
	}
	return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMessageType activateUserEmailAddress(String email, String nonce) {
	logger.log(Level.WARNING, "activateUserEmailAddress: " + email + ", " + nonce);
	try {
	    if (StringUtils.isBlank(email) || StringUtils.isBlank(nonce)) {
		return ReturnMessageType.no_data;
	    }
	    UserProfile userProfile = userProfileDAO.findByField("nonce", nonce);
	    if (userProfile == null) {
		return ReturnMessageType.not_found;
	    }
	    ReturnMessageType result = ReturnMessageType.validation_failed;
	    if (StringUtils.isNotBlank(userProfile.getNewEmail())) {
		// email address change
		if (userProfile.getNewEmail().equals(email)) {
		    String serverNonce = SecurityUtils.getHashWithoutSalt(userProfile.getNewEmail() + ":"
			    + userProfile.getSalt());
		    if (serverNonce.equals(nonce)) {
			userProfile.setEmail(userProfile.getNewEmail());
			userProfile.setNewEmail(null);
			userProfile.setNonce(null);
			userProfileDAO.merge(userProfile);
			result = ReturnMessageType.success;
		    }
		}
	    } else {
		// new user
		if (userProfile.getEmail().equals(email)) {
		    String serverNonce = SecurityUtils.getHashWithoutSalt(userProfile.getEmail() + ":"
			    + userProfile.getSalt());
		    if (serverNonce.equals(nonce)) {
			userProfile.setUserProfileStatus(UserProfileStatusType.active);
			userProfile.setNonce(null);
			userProfile.getDescriptionContent().setAuthorUsername(userProfile.getUsername());
			userProfile.getDescriptionContent().setContentStatus(ContentStatusType.active);
			userProfile.setSubscribed(true);
			userProfileDAO.merge(userProfile);
			result = ReturnMessageType.success;
		    }
		}
	    }
	    return result;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "activateUserEmailAddress exception: " + ex);
	    return ReturnMessageType.exception;
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReturnMessageType sendNewPassword(String username, String email) {
	try {
	    if (StringUtils.isBlank(username) || StringUtils.isBlank(email)) {
		return ReturnMessageType.no_data;
	    }
	    UserProfile recipientUserProfile = userProfileDAO.findByField("username", username);
	    if (recipientUserProfile == null || !recipientUserProfile.getEmail().equals(email)) {
		return ReturnMessageType.not_found;
	    }
	    SecureRandom random = new SecureRandom();
	    String newPassword = new BigInteger(130, random).toString(32);
	    List<String> hashResult = SecurityUtils.getHashWithSalt(newPassword);
	    if ((hashResult != null) && (hashResult.size() == 2)) {
		recipientUserProfile.setPassword(hashResult.get(0));
		recipientUserProfile.setSalt(hashResult.get(1));
		userProfileDAO.merge(recipientUserProfile);
	    }
	    // TODO i18n
	    String emailSubject = "nova lozinka za bundolo";
	    String emailBody = "pozdrav, <br/>"
		    + "tražili ste novu lozinku za vaš bundolo korisnički nalog.<br/>"
		    + "prilikom sledećeg prijavljivanja, koristite sledeće podatke:<br/>"
		    + "korisničko ime: "
		    + recipientUserProfile.getUsername()
		    + "<br/>lozinka: "
		    + newPassword
		    + "<br/>da biste povećali sigurnost vašeg korisničkoh naloga, promenite ovu lozinku što je pre moguće.<br/><br/>"
		    + "poštovanje,<br/>bundolo administracija";
	    mailingUtils.sendEmail(emailBody, emailSubject, recipientUserProfile.getEmail());
	    return ReturnMessageType.success;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "sendNewPassword exception: " + ex);
	    return ReturnMessageType.exception;
	}
    }

    @Override
    public ReturnMessageType sendMessage(String title, String text, String recipientUsername) {
	logger.log(Level.WARNING, "sendMessage: " + title + ", " + recipientUsername);
	try {
	    String senderUsername = SecurityUtils.getUsername();
	    String recipientEmailAddress;
	    if (StringUtils.isNotBlank(recipientUsername)) {
		if (StringUtils.isBlank(senderUsername)) {
		    // guests can't send messages to users
		    return ReturnMessageType.anonymous_not_allowed;
		}
		UserProfile recipientUserProfile = userProfileDAO.findByField("username", recipientUsername);
		if (recipientUserProfile != null) {
		    recipientEmailAddress = recipientUserProfile.getEmail();
		} else {
		    // recipient does not exist
		    return ReturnMessageType.not_found;
		}
	    } else {
		recipientEmailAddress = properties.getProperty("mail.to");
	    }
	    if (StringUtils.isBlank(senderUsername)) {
		senderUsername = Constants.DEFAULT_GUEST_USERNAME;
	    }
	    String emailSubject = "bundolo (NO REPLAY) privatna poruka od korisnika " + senderUsername + ": " + title;
	    String emailBody = text
		    + "<br/><br/>---<br/>"
		    + "ovo je privatna poruka poslata sa stranice bundolo.org od korisnika "
		    + senderUsername
		    + ".<br/>"
		    + "adrese korisnika su sakrivene, na poruku možete odgovoriti slanjem privatne poruke sa bundola, a NE povratnom porukom (replay).<br/><br/>"
		    + "poštovanje,<br/>bundolo administracija";
	    mailingUtils.sendEmail(emailBody, emailSubject, recipientEmailAddress);
	    return ReturnMessageType.success;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "sendMessage exception: " + ex);
	    return ReturnMessageType.exception;
	}
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private ResponseEntity<String> saveUser(String username, String email, String password) {
	try {
	    if (StringUtils.isBlank(username) || StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
		return new ResponseEntity<String>(ReturnMessageType.no_data.name(), HttpStatus.BAD_REQUEST);
	    }
	    UserProfile userProfile = userProfileDAO.findByField("username", username);
	    if (userProfile != null) {
		return new ResponseEntity<String>(ReturnMessageType.username_taken.name(), HttpStatus.BAD_REQUEST);
	    }
	    // TODO slug
	    userProfile = userProfileDAO.findByField("email", email);
	    if (userProfile != null) {
		return new ResponseEntity<String>(ReturnMessageType.email_taken.name(), HttpStatus.BAD_REQUEST);
	    }
	    userProfile = userProfileDAO.findByField("new_email", email);
	    if (userProfile != null) {
		return new ResponseEntity<String>(ReturnMessageType.email_taken.name(), HttpStatus.BAD_REQUEST);
	    }
	    userProfile = new UserProfile();
	    userProfile.setEmail(email);
	    userProfile.setUsername(username);
	    userProfile.setPassword(password);
	    userProfile.setUserProfileStatus(UserProfileStatusType.pending);
	    userProfile.setSignupDate(dateUtils.newDate());
	    userProfile.setLastIp(getRemoteHost());
	    userProfile.setSubscribed(false);
	    userProfile.setNewsletterSendingDate(userProfile.getSignupDate());

	    Date creationDate = dateUtils.newDate();
	    Content descriptionContent = new Content(null, null, ContentKindType.user_description, null, "",
		    Constants.DEFAULT_LOCALE, creationDate, creationDate, ContentStatusType.pending, null,
		    ContentKindType.user_description.getLocalizedName() + "/" + username);
	    userProfile.setDescriptionContent(descriptionContent);

	    List<String> hashResult = SecurityUtils.getHashWithSalt(password);
	    if ((hashResult != null) && (hashResult.size() == 2)) {
		userProfile.setPassword(hashResult.get(0));
		userProfile.setSalt(hashResult.get(1));
	    }
	    String nonce = SecurityUtils.getHashWithoutSalt(email + ":" + userProfile.getSalt());
	    userProfile.setNonce(nonce);
	    userProfileDAO.persist(userProfile);
	    String activationUrl = properties.getProperty("application.root") + "/validate?nonce=" + nonce + "&email="
		    + email;
	    // TODO i18n
	    // TODO backlog: implement manual activation
	    String emailBody = "pozdrav, "
		    + username
		    + ","
		    + "<br/><br/>"
		    + "neko, verovatno vi, se registrovao na sajtu bundolo.org<br/>"
		    + "da biste potvrdili ispravnost ove adrese elektronske pošte i aktivirali svoj nalog, dovoljno je da kliknete na donji link.<br/><br/>"
		    + activationUrl
		    + "<br/><br/>ukoliko vam link nije aktivan i ne može se kliknuti, možete ga kopirati i otvoriti u browseru.<br/><br/>"
		    + "poštovanje,<br/>bundolo administracija";
	    // + "<br/><br/>If you prefer to enter this information manually, go to http://www.bundolo.org and<br/>"
	    // + "enter the following Auth code:<br/><br/>" + nonce;
	    String emailSubject = "aktivacija bundolo korisničkog naloga";
	    mailingUtils.sendEmail(emailBody, emailSubject, email);
	    // TODO rollback db if email sending failed, or notify admin somehow
	    return new ResponseEntity<String>(ReturnMessageType.title_taken.name(), HttpStatus.OK);
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveUser exception: " + ex);
	    return new ResponseEntity<String>(ReturnMessageType.exception.name(), HttpStatus.BAD_REQUEST);
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseEntity<String> saveOrUpdateUser(UserProfile userProfile) {
	try {
	    if (userProfile == null || StringUtils.isBlank(userProfile.getUsername())) {
		return new ResponseEntity<String>(ReturnMessageType.no_data.name(), HttpStatus.BAD_REQUEST);
	    }
	    UserProfile userProfileDB = userProfileDAO.findByField("username", userProfile.getUsername());
	    if (userProfileDB == null) {
		return saveUser(userProfile.getUsername(), userProfile.getEmail(), userProfile.getPassword());
	    }
	    String senderUsername = SecurityUtils.getUsername();
	    if (!userProfile.getUsername().equals(senderUsername)) {
		// user is not the owner of the account he is updating
		// TODO if this was registration and username is already taken, it will fail here instead of in saveUser
		// return ReturnMessageType.not_owner;
		return new ResponseEntity<String>(ReturnMessageType.username_taken.name(), HttpStatus.BAD_REQUEST);
	    }
	    if (StringUtils.isNotBlank(userProfile.getNewEmail())) {
		UserProfile testUserProfile = null;
		testUserProfile = userProfileDAO.findByField("email", userProfile.getNewEmail());
		if (testUserProfile != null) {
		    return new ResponseEntity<String>(ReturnMessageType.email_taken.name(), HttpStatus.BAD_REQUEST);
		}
		testUserProfile = userProfileDAO.findByField("new_email", userProfile.getNewEmail());
		if (testUserProfile != null) {
		    return new ResponseEntity<String>(ReturnMessageType.email_taken.name(), HttpStatus.BAD_REQUEST);
		}
	    }

	    Content descriptionContent = userProfileDB.getDescriptionContent();
	    Date creationDate = dateUtils.newDate();
	    // comment out this check to decrease complexity. if it proves to be safe from npe, remove it completely
	    // if (descriptionContent == null) {
	    // descriptionContent = new Content(null, null, ContentKindType.user_description, null, "",
	    // Constants.DEFAULT_LOCALE, creationDate, creationDate, ContentStatusType.active, null);
	    // } else {
	    // logger.log(Level.WARNING, "setLastActivity: " + creationDate);
	    descriptionContent.setLastActivity(creationDate);
	    // }
	    if (userProfile.getDescriptionContent() != null) {
		if (StringUtils.isNotBlank(userProfile.getDescriptionContent().getText())) {
		    descriptionContent.setText(userProfile.getDescriptionContent().getText());
		}
		userProfileDB.setDescriptionContent(descriptionContent);

		if (StringUtils.isNotBlank(userProfile.getPassword())
			&& !SecurityUtils.getHashWithPredefinedSalt(userProfile.getPassword(), userProfileDB.getSalt())
				.equals(userProfileDB.getPassword())) {
		    List<String> hashResult = SecurityUtils.getHashWithSalt(userProfile.getPassword());
		    if ((hashResult != null) && (hashResult.size() == 2)) {
			userProfileDB.setPassword(hashResult.get(0));
			userProfileDB.setSalt(hashResult.get(1));
		    }
		}
		userProfileDB.setFirstName(userProfile.getFirstName());
		userProfileDB.setLastName(userProfile.getLastName());
		userProfileDB.setBirthDate(userProfile.getBirthDate());
		userProfileDB.setGender(userProfile.getGender());
		if ((StringUtils.isNotBlank(userProfile.getNewEmail()))
			&& (!userProfileDB.getEmail().equals(userProfile.getNewEmail()))) {
		    userProfileDB.setNewEmail(userProfile.getNewEmail());
		    String nonce = SecurityUtils.getHashWithoutSalt(userProfileDB.getNewEmail() + ":"
			    + userProfileDB.getSalt());
		    userProfileDB.setNonce(nonce);
		} else {
		    userProfileDB.setNewEmail(null);
		}
		userProfileDB.setShowPersonal(userProfile.getShowPersonal());
		userProfileDB.setAvatarUrl(userProfile.getAvatarUrl());
		userProfileDB.setSubscribed(userProfile.getSubscribed());
	    }

	    userProfileDAO.merge(userProfileDB);
	    if (StringUtils.isNotBlank(userProfileDB.getNewEmail())) {
		String activationUrl = properties.getProperty("application.root") + "/validate?nonce="
			+ userProfileDB.getNonce() + "&email=" + userProfileDB.getNewEmail();
		// TODO i18n
		// TODO backlog: implement manual activation
		String emailBody = "pozdrav, "
			+ userProfileDB.getUsername()
			+ ",<br/><br/>"
			+ "neko, verovatno vi, je zatražio izmenu adrese elektronske pošte na vašem bundolo korisničkom nalogu.<br/>"
			+ "da biste potvrdili ispravnost ove adrese, dovoljno je da kliknete na donji link.<br/><br/>"
			+ activationUrl
			+ "<br/><br/>ukoliko vam link nije aktivan i ne može se kliknuti, možete ga kopirati i otvoriti u browseru.<br/><br/>"
			+ "poštovanje,<br/>bundolo administracija";
		// + "<br/><br/>If you prefer to enter this information manually, go to http://www.bundolo.org and<br/>"
		// + "enter the following Auth code:<br/><br/>" + userProfileDB.getNonce();
		String emailSubject = "aktivacija nove adrese elektronske pošte za bundolo korisnički nalog";
		mailingUtils.sendEmail(emailBody, emailSubject, userProfile.getNewEmail());
	    }
	    return new ResponseEntity<String>(userProfileDB.getDescriptionContent().getSlug(), HttpStatus.OK);
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "updateUser exception: " + ex);
	    return new ResponseEntity<String>(ReturnMessageType.exception.name(), HttpStatus.BAD_REQUEST);
	}
    }

    @Override
    public void clearSession() {
	userDAO.clear();
    }

    @Override
    public User findNext(String username, String orderBy, String fixBy, boolean ascending) {
	return userDAO.findNext(username, orderBy, fixBy, ascending);
    }

    @Override
    public Long deleteUser(String username) {
	// TODO
	return null;
    }

    private String getRemoteHost() {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	return sra.getRequest().getRemoteHost();
    }
}
