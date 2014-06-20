package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.MailingUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.SessionUtils;
import org.bundolo.Utils;
import org.bundolo.dao.UserDAO;
import org.bundolo.dao.UserProfileDAO;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.UserProfileStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserProfileServiceImpl.class.getName());

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
    public User findUser(String username) {
	return userDAO.findById(username);
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
    public Boolean activateUserProfileEmailAddress(String email, String nonce) {
	logger.log(Level.INFO, "activateUserProfileEmailAddress: " + email + ", " + nonce);
	Boolean result = false;
	try {
	    if (Utils.hasText(email) && Utils.hasText(nonce)) {
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
	    ex.printStackTrace();
	}
	return result;
    }

    @Override
    public Boolean sendNewPassword(String email) {
	try {
	    UserProfile recipientUserProfile = userProfileDAO.findByField("email", email);
	    if (recipientUserProfile == null) {
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
		return true;
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	return false;
    }

    @Override
    public Boolean sendMessage(String title, String text, String recipientUsername) {
	try {
	    UserProfile recipientUserProfile = userProfileDAO.findByField("username", recipientUsername);
	    if (recipientUserProfile != null) {
		// TODO i18n
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

}
