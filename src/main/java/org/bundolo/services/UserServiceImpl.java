package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.MailingUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.SessionUtils;
import org.bundolo.Utils;
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
import org.bundolo.model.enumeration.UserProfileStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private MailingUtils mailingUtils;

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
	    Rating rating = user.getDescriptionContent().getRating();
	    if (rating == null) {
		rating = new Rating(null, null, RatingKindType.general, new Date(), RatingStatusType.active,
			Constants.DEFAULT_RATING_INCREMENT, user.getDescriptionContent());
		user.getDescriptionContent().setRating(rating);
	    } else {
		rating.setValue(rating.getValue() + Constants.DEFAULT_RATING_INCREMENT);
		rating.setLastActivity(new Date());
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
    public Boolean authenticateUser(String username, String password) {
	logger.log(Level.WARNING, "login: " + username + ", " + password);
	Boolean result = false;
	try {
	    UserProfile userProfile = userProfileDAO.findByField("username", username);
	    if ((userProfile != null) && (UserProfileStatusType.active.equals(userProfile.getUserProfileStatus()))) {
		// TODO once password hashing is implemented, we will check password
		// differently
		if (password.equals(userProfile.getPassword())) {
		    userProfile.setLastLoginDate(new Date());
		    // this line fails
		    userProfile.setLastIp(SessionUtils.getRemoteHost());
		    userProfileDAO.merge(userProfile);
		    result = true;
		}
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "authenticateUser exception: " + ex);
	}
	return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean activateUserEmailAddress(String email, String nonce) {
	logger.log(Level.WARNING, "activateUserEmailAddress: " + email + ", " + nonce);
	Boolean result = false;
	try {
	    if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(nonce)) {
		UserProfile userProfile = userProfileDAO.findByField("nonce", nonce);
		if (userProfile != null) {
		    String serverNonce;
		    if (Utils.hasText(userProfile.getNewEmail())) {
			if (userProfile.getNewEmail().equals(email)) {
			    serverNonce = SecurityUtils.getHashWithoutSalt(userProfile.getNewEmail() + ":"
				    + userProfile.getSalt());
			    if (serverNonce.equals(nonce)) {
				userProfile.setEmail(userProfile.getNewEmail());
				userProfile.setNewEmail(null);
				result = true;
			    }
			}
		    } else {
			if (userProfile.getEmail().equals(email)) {
			    serverNonce = SecurityUtils.getHashWithoutSalt(userProfile.getEmail() + ":"
				    + userProfile.getSalt());
			    if (serverNonce.equals(nonce)) {
				userProfile.setUserProfileStatus(UserProfileStatusType.active);
				result = true;
			    }
			}
		    }
		    userProfile.setNonce(null);
		    userProfileDAO.merge(userProfile);
		}
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "activateUserEmailAddress exception: " + ex);
	    result = false;
	}
	return result;
    }

    @Override
    public Boolean sendNewPassword(String username, String email) {
	boolean result = false;
	try {
	    if (StringUtils.isNotBlank(username)) {
		UserProfile recipientUserProfile = userProfileDAO.findByField("username", username);
		if (recipientUserProfile == null || !recipientUserProfile.getEmail().equals(email)) {
		    // ServerValidation.exception(LabelType.user_not_found.name(), LabelType.email_address.name());
		    // TODO
		} else {
		    // TODO instead of just sending the password, generate new, save it
		    // to database and send it to the user
		    // TODO i18n
		    String emailSubject = "New bundolo password";
		    String emailBody = "You requested a password reset for your bundolo account.\n"
			    + "Next time you login, you can use the following:\n" + "Username: "
			    + recipientUserProfile.getUsername() + "\nPassword: " + recipientUserProfile.getPassword()
			    + "\nTo increase safety of your account, please change your password as soon as possible.";
		    mailingUtils.sendEmail(emailBody, emailSubject, recipientUserProfile.getEmail());
		    result = true;
		}
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "sendNewPassword exception: " + ex);
	}
	return result;
    }

    @Override
    public Boolean sendMessage(String title, String text, String recipientUsername) {
	try {
	    UserProfile recipientUserProfile = userProfileDAO.findByField("username", recipientUsername);
	    if (recipientUserProfile != null) {
		// TODO i18n
		// TODO do not retrieve username from session
		String emailSubject = "bundolo user " + SessionUtils.getUsername() + " sent you a message: " + title;
		String emailBody = "Message text:\n" + text + "\n\nPlease use bundolo to reply!";
		mailingUtils.sendEmail(emailBody, emailSubject, recipientUserProfile.getEmail());
		return true;
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	return false;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Boolean saveUser(String username, String email, String password) {
	try {
	    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(email) && StringUtils.isNotBlank(password)) {
		UserProfile userProfile = userProfileDAO.findByField("username", username);
		if (userProfile != null) {
		    return false;
		}
		userProfile = userProfileDAO.findByField("email", email);
		if (userProfile != null) {
		    return false;
		}
		userProfile = userProfileDAO.findByField("new_email", email);
		if (userProfile != null) {
		    return false;
		}
		userProfile = new UserProfile();
		userProfile.setEmail(email);
		userProfile.setUsername(username);
		userProfile.setPassword(password);
		userProfile.setUserProfileStatus(UserProfileStatusType.pending);
		userProfile.setSignupDate(new Date());
		userProfile.setLastIp(SessionUtils.getRemoteHost());

		Date creationDate = new Date();
		Content descriptionContent = new Content(null, username, ContentKindType.user_description, null, "",
			Constants.DEFAULT_LOCALE, creationDate, creationDate, ContentStatusType.active, null);
		userProfile.setDescriptionContent(descriptionContent);

		List<String> hashResult = SecurityUtils.getHashWithSalt(password);
		if ((hashResult != null) && (hashResult.size() == 2)) {
		    // TODO enable password hashing later
		    // userProfile.setPassword(hashResult.get(0));
		    userProfile.setSalt(hashResult.get(1));
		}
		String nonce = SecurityUtils.getHashWithoutSalt(email + ":" + userProfile.getSalt());
		if (nonce != null) {
		    userProfile.setNonce(nonce);
		    userProfileDAO.persist(userProfile);

		    // TODO compose this url
		    String activationUrl = "http://localhost/validate?nonce=" + nonce + "&email=" + email;
		    // TODO i18n
		    // TODO backlog: implement manual activation
		    String emailBody = "Hi " + username + "\n\n" + "Someone, probably you, registered at Bundolo\n"
			    + "Please use the following URL to complete the registration.\n\n" + activationUrl;
		    // + "\n\nIf you prefer to enter this information manually, go to http://www.bundolo.org and\n"
		    // + "enter the following Auth code:\n\n" + nonce;
		    String emailSubject = "Email activation for bundolo";
		    mailingUtils.sendEmail(emailBody, emailSubject, email);
		    return true;
		}
	    }
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "saveUser exception: " + ex);
	}
	return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean saveOrUpdateUser(UserProfile userProfile) {
	try {
	    if (userProfile == null || StringUtils.isBlank(userProfile.getUsername())) {
		return false;
	    }
	    UserProfile userProfileDB = userProfileDAO.findByField("username", userProfile.getUsername());
	    if (userProfileDB == null) {
		return saveUser(userProfile.getUsername(), userProfile.getEmail(), userProfile.getPassword());
	    }

	    if (StringUtils.isNotBlank(userProfile.getNewEmail())) {
		UserProfile testUserProfile = null;
		testUserProfile = userProfileDAO.findByField("email", userProfile.getNewEmail());
		if (testUserProfile != null) {
		    return false;
		}
	    }

	    Content descriptionContent = userProfileDB.getDescriptionContent();
	    Date creationDate = new Date();
	    if (descriptionContent == null) {
		descriptionContent = new Content(null, null, ContentKindType.user_description, null, "",
			Constants.DEFAULT_LOCALE, creationDate, creationDate, ContentStatusType.active, null);

	    } else {
		descriptionContent.setLastActivity(creationDate);
	    }
	    if (StringUtils.isNotBlank(userProfile.getDescriptionContent().getText())) {
		descriptionContent.setText(userProfile.getDescriptionContent().getText());
	    }
	    userProfileDB.setDescriptionContent(descriptionContent);

	    // TODO this check will look differently when hashing is implemented
	    if ((StringUtils.isNotBlank(userProfile.getPassword()))
		    && (!userProfileDB.getPassword().equals(userProfile.getPassword()))) {
		List<String> hashResult = SecurityUtils.getHashWithSalt(userProfile.getPassword());
		if ((hashResult != null) && (hashResult.size() == 2)) {
		    // TODO enable password hashing later
		    // userProfileDB.setPassword(hashResult.get(0));
		    userProfileDB.setPassword(userProfile.getPassword());
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
	    // userProfileDB.setSignupDate(userProfile.getSignupDate());
	    // userProfileDB.setLastLoginDate(userProfile.getLastLoginDate());
	    // userProfile.setLastIp(userProfileDTO.getLastIp());
	    // userProfile.setUserProfileStatus(userProfileDTO.getUserProfileStatus());
	    userProfileDB.setAvatarUrl(userProfile.getAvatarUrl());
	    userProfileDAO.merge(userProfileDB);
	    // userProfileDAO.clear();
	    if (StringUtils.isNotBlank(userProfileDB.getNewEmail())) {
		// TODO compose this url
		String activationUrl = "http://localhost/validate?nonce=" + userProfileDB.getNonce() + "&email="
			+ userProfileDB.getNewEmail();
		// TODO i18n
		// TODO backlog: implement manual activation
		String emailBody = "Hi " + userProfileDB.getUsername() + "\n\n"
			+ "Someone, probably you, updated email address at bundolo\n"
			+ "Please use the following URL to verify the address.\n\n" + activationUrl;
		// + "\n\nIf you prefer to enter this information manually, go to http://www.bundolo.org and\n"
		// + "enter the following Auth code:\n\n" + userProfileDB.getNonce();
		String emailSubject = "New email activation for bundolo";
		mailingUtils.sendEmail(emailBody, emailSubject, userProfile.getNewEmail());
	    }
	    return true;
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "updateUser exception: " + ex);
	}
	return false;
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
}
