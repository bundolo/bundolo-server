package org.bundolo.services;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.MailingUtils;
import org.bundolo.SecurityUtils;
import org.bundolo.SessionUtils;
import org.bundolo.Utils;
import org.bundolo.dao.ContentDAO;
import org.bundolo.dao.UserProfileDAO;
import org.bundolo.model.Content;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContentStatusType;
import org.bundolo.model.enumeration.UserProfileStatusType;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userProfileService")
public class UserProfileServiceImpl implements UserProfileService, ApplicationContextAware {

    private static final Logger logger = Logger.getLogger(UserProfileServiceImpl.class.getName());

    private ApplicationContext applicationContext;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private ContentDAO contentDAO;

    @Autowired
    private ContentService contentService;

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    public UserProfile findUserProfile(Long userId) {
	UserProfile result = userProfileDAO.findById(userId);
	return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveUserProfile(UserProfile userProfile) throws Exception {
	UserProfile userProfileDB = null;
	if (Utils.hasText(userProfile.getUsername())) {
	    userProfileDB = userProfileDAO.findByField("username", userProfile.getUsername());
	}
	if (userProfileDB != null) {
	    // ServerValidation.exception(LabelType.display_name_already_exists.name(), LabelType.display_name.name());
	    // TODO throw exception, report error
	}
	if (Utils.hasText(userProfile.getEmail())) {
	    userProfileDB = userProfileDAO.findByField("new_email", userProfile.getEmail());
	}
	if (userProfileDB != null) {
	    // ServerValidation.exception(LabelType.email_already_exists.name(), LabelType.email_address.name());
	    // TODO throw exception, report error
	}
	// userProfileDB = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile, UserProfile.class);
	userProfile.setUserProfileStatus(UserProfileStatusType.pending);
	userProfile.setSignupDate(new Date());

	Content descriptionContent = new Content(null, userProfile.getUsername(), ContentKindType.user_description,
		null, "", SessionUtils.getUserLocale(), new Date(), ContentStatusType.active, null);
	// Long contentId = contentService.saveContent(descriptionContent);
	userProfile.setDescriptionContent(descriptionContent);
	userProfile.setLastIp(SessionUtils.getRemoteHost());
	List<String> hashResult = SecurityUtils.getHashWithSalt(userProfile.getPassword());
	if ((hashResult != null) && (hashResult.size() == 2)) {
	    // TODO enable password hashing later
	    // userProfile.setPassword(hashResult.get(0));
	    userProfile.setSalt(hashResult.get(1));
	}
	String nonce = SecurityUtils.getHashWithoutSalt(userProfile.getEmail() + ":" + userProfile.getSalt());
	if (nonce != null) {
	    userProfile.setNonce(nonce);
	    try {
		userProfileDAO.persist(userProfile);
	    } catch (Exception ex) {
		// contentService.deleteContent(contentId);
		// if (ex.getMessage().contains("user_profile_email_key")) {
		// ServerValidation.exception(LabelType.email_already_exists.name(), LabelType.email_address.name());
		// } else {
		// throw new Exception("db exception");
		// }
		throw new Exception("db exception");
	    }

	    // TODO compose this url
	    String activationUrl = "http://127.0.0.1:8888/Bundolo.html?gwt.codesvr=127.0.0.1:9997&email="
		    + userProfile.getEmail() + "&nonce=" + nonce + "#activate";
	    // TODO i18n
	    String emailBody = "Hi " + userProfile.getUsername() + "\n\n"
		    + "Someone, probably you, registered at Bundolo\n"
		    + "Please use the following URL to complete the registration.\n\n" + activationUrl
		    + "\n\nIf you prefer to enter this information manually, go to http://www.bundolo.org and\n"
		    + "enter the following Auth code:\n\n" + nonce;
	    String emailSubject = "Email activation for bundolo";
	    MailingUtils mailingUtils = (MailingUtils) applicationContext.getBean("mailingUtils");
	    mailingUtils.sendEmail(emailBody, emailSubject, userProfile.getEmail());
	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User updateUserProfile(UserProfile userProfile) throws Exception {
	User result = null;
	UserProfile userProfileDB = userProfileDAO.findByField("session_id", userProfile.getSessionId());
	if (userProfileDB == null) {
	    throw new Exception("no permission exception");
	}
	UserProfile testUserProfile = null;
	if (Utils.hasText(userProfile.getNewEmail())) {
	    testUserProfile = userProfileDAO.findByField("email", userProfile.getNewEmail());
	}
	if (testUserProfile != null) {
	    // ServerValidation.exception(LabelType.email_already_exists.name(), LabelType.new_email.name());
	    // TODO
	}
	// ContentDTO descriptionContent = userProfileDTO.getDescriptionContent();
	// if (descriptionContent != null) {
	// if (descriptionContent.getContentId() == null) {
	// Long descriptionContentId = contentService.saveContent(userProfileDTO.getDescriptionContent());
	// /*
	// * this is not going to be used at the moment, but it will when
	// * we add more locales, to add descriptions in different
	// * languages
	// */
	// userProfileDB.setDescriptionContentId(descriptionContentId);
	// } else {
	// contentService.updateContent(userProfileDTO.getDescriptionContent());
	// }
	// }
	userProfileDB.setUsername(userProfile.getUsername());
	// TODO this check will look differently when hashing is implemented
	if ((Utils.hasText(userProfile.getPassword()))
		&& (!userProfileDB.getPassword().equals(userProfile.getPassword()))) {
	    // TODO check this, it compares password with email, it might be ok if field is used for some temporary
	    // value
	    if (!userProfileDB.getPassword().equals(userProfile.getEmail())) {
		// ServerValidation.exception(LabelType.validator_wrongPassword.name(),
		// LabelType.current_password.name());
		// TODO
	    }
	    // TODO hash password, set salt
	    userProfileDB.setPassword(userProfile.getPassword());
	    userProfileDB.setSalt(userProfile.getSalt());
	}
	userProfileDB.setFirstName(userProfile.getFirstName());
	userProfileDB.setLastName(userProfile.getLastName());
	userProfileDB.setBirthDate(userProfile.getBirthDate());
	userProfileDB.setGender(userProfile.getGender());
	if ((Utils.hasText(userProfile.getNewEmail())) && (!userProfileDB.getEmail().equals(userProfile.getNewEmail()))) {
	    userProfileDB.setNewEmail(userProfile.getNewEmail());
	    String nonce = SecurityUtils
		    .getHashWithoutSalt(userProfileDB.getNewEmail() + ":" + userProfileDB.getSalt());
	    userProfileDB.setNonce(nonce);
	} else {
	    userProfileDB.setNewEmail(null);
	}
	userProfileDB.setShowPersonal(userProfile.getShowPersonal());
	userProfileDB.setSignupDate(userProfile.getSignupDate());
	userProfileDB.setLastLoginDate(userProfile.getLastLoginDate());
	// userProfile.setLastIp(userProfileDTO.getLastIp());
	// userProfile.setUserProfileStatus(userProfileDTO.getUserProfileStatus());
	userProfileDB.setAvatarUrl(userProfile.getAvatarUrl());
	userProfileDB.setSessionId(userProfile.getSessionId());
	userProfileDB.setDescriptionContent(userProfile.getDescriptionContent());
	try {
	    userProfileDAO.merge(userProfileDB);
	} catch (Exception ex) {
	    if (ex.getMessage().contains("user_profile_email_key")) {
		// ServerValidation.exception(LabelType.email_already_exists.name(), LabelType.email_address.name());
		// TODO
	    } else if (ex.getMessage().contains("user_profile_username_key")) {
		// ServerValidation.exception(LabelType.display_name_already_exists.name(),
		// LabelType.display_name.name());
		// TODO
	    } else {
		throw new Exception("db exception");
	    }
	}
	if ((Utils.hasText(userProfile.getNewEmail())) && (!userProfileDB.getEmail().equals(userProfile.getNewEmail()))) {
	    // TODO compose this url
	    String activationUrl = "http://127.0.0.1:8888/Bundolo.html?gwt.codesvr=127.0.0.1:9997&email="
		    + userProfileDB.getNewEmail() + "&nonce=" + userProfileDB.getNonce() + "#activate";
	    // TODO i18n
	    String emailBody = "Hi " + userProfileDB.getUsername() + "\n\n"
		    + "Someone, probably you, updated email address at bundolo\n"
		    + "Please use the following URL to verify the address.\n\n" + activationUrl
		    + "\n\nIf you prefer to enter this information manually, go to http://www.bundolo.org and\n"
		    + "enter the following Auth code:\n\n" + userProfileDB.getNonce();
	    String emailSubject = "New email activation for bundolo";
	    MailingUtils mailingUtils = (MailingUtils) applicationContext.getBean("mailingUtils");
	    mailingUtils.sendEmail(emailBody, emailSubject, userProfile.getNewEmail());
	}
	result = DozerBeanMapperSingletonWrapper.getInstance().map(userProfileDB, User.class);
	// Content descriptionContentUpdated = contentDAO.findContentForLocale(userProfileDB.getDescriptionContentId(),
	// ContentKindType.user_description, SessionUtils.getUserLocale());
	// if (descriptionContentUpdated != null) {
	// result.setDescriptionContent(DozerBeanMapperSingletonWrapper.getInstance().map(descriptionContentUpdated,
	// ContentDTO.class));
	// }
	return result;
    }

    // @Transactional(propagation=Propagation.REQUIRED,
    // rollbackFor=Exception.class)
    // private UserDTO updateUserProfileByUsername(UserProfileDTO
    // userProfileDTO) throws Exception {
    // UserDTO result = null;
    // UserProfile userProfile = userProfileDAO.findByField("username",
    // userProfileDTO.getUsername());
    // if(userProfile == null) {
    // throw new Exception("no permission exception");
    // }
    // userProfileDAO.merge(userProfile);
    //
    // return updateUserProfile(userProfileDTO, userProfile);
    // }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUserProfile(Long userId) throws Exception {
	UserProfile userProfile = userProfileDAO.findById(userId);

	if (userProfile != null)
	    userProfileDAO.remove(userProfile);
    }

    // @Override
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // public String login(String username, String password, Boolean rememberMe) throws Exception {
    // logger.log(Level.INFO, "login: " + username + ", " + password + ", " + rememberMe);
    // String result = "";
    // // TODO implement this. use owasp code from
    // // https://www.owasp.org/index.php/Hashing_Java
    // // some of the required classes might not be visible in gwt so read this
    // // http://code.google.com/p/google-web-toolkit-incubator/wiki/LoginSecurityFAQ
    // UserProfile userProfile = userProfileDAO.findByField("username", username);
    // if ((userProfile != null) && (UserProfileStatusType.active.equals(userProfile.getUserProfileStatus()))) {
    // // TODO once password hashing is implemented, we will check password
    // // differently
    // if (password.equals(userProfile.getPassword())) {
    // // we are switching to stateless approach so no session data on server
    // // userProfile.setSessionId(SessionUtils.getSessionId());
    // userProfile.setLastLoginDate(new Date());
    // // this line fails
    // userProfile.setLastIp(SessionUtils.getRemoteHost());
    //
    // userProfileDAO.merge(userProfile);
    //
    // UserDTO userDTO = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile, UserDTO.class);
    // UserProfileDTO userProfileDTO = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile,
    // UserProfileDTO.class);
    //
    // Content descriptionContent = contentDAO.findContentForLocale(userProfile.getDescriptionContentId(),
    // ContentKindType.user_description, SessionUtils.getUserLocale());
    // if (descriptionContent != null) {
    // ContentDTO descriptionContentDTO = DozerBeanMapperSingletonWrapper.getInstance().map(
    // descriptionContent, ContentDTO.class);
    // userDTO.setDescriptionContent(descriptionContentDTO);
    // userProfileDTO.setDescriptionContent(descriptionContentDTO);
    // }
    // result = userDTO;
    //
    // if (rememberMe) {
    // Calendar calendar = Calendar.getInstance();
    // calendar.add(Calendar.DATE, 14);
    // String rememberMeHashDecoded = SecurityUtils.getHashWithoutSalt(userProfileDTO.getUsername() + ":"
    // + calendar.getTimeInMillis() + ":" + userProfile.getSalt());
    // String rememberMeCompleteDecoded = userProfileDTO.getUsername() + ":" + calendar.getTimeInMillis()
    // + ":" + rememberMeHashDecoded;
    // result.setRememberMeCookie(Base64.encodeBase64URLSafeString(rememberMeCompleteDecoded
    // .getBytes("UTF-8")));
    // }
    // SessionUtils.setAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME, userProfileDTO.getSessionId());
    // SessionUtils.setAttribute(Constants.USER_PROFILE_ATTRIBUTE_NAME, userProfileDTO);
    // }
    // }
    // return result;
    // }

    // @Override
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // public void logout() throws Exception {
    // String sessionId = findCookieValue(Constants.SESSION_ID_COOKIE_NAME);
    // String serverSessionId = (String) SessionUtils.getAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME);
    // if (Utils.hasText(serverSessionId) && serverSessionId.equals(sessionId)) {
    // UserProfile userProfile = userProfileDAO.findByField("session_id", sessionId);
    // if (userProfile != null) {
    // userProfile.setSessionId(null);
    // userProfileDAO.merge(userProfile);
    // SessionUtils.removeAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME);
    // SessionUtils.removeAttribute(Constants.USER_PROFILE_ATTRIBUTE_NAME);
    // }
    // }
    // }

    // @Override
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // public Boolean activateUserProfileEmailAddress(String email, String nonce) throws Exception {
    // logger.log(Level.INFO, "activateUserProfileEmailAddress: " + email + ", " + nonce);
    // Boolean result = false;
    // if (Utils.hasText(email) && Utils.hasText(nonce)) {
    // UserProfile userProfile = userProfileDAO.findByField("nonce", nonce);
    // if (userProfile != null) {
    // String serverNonce;
    // if (Utils.hasText(userProfile.getNewEmail())) {
    // if (userProfile.getNewEmail().equals(email)) {
    // serverNonce = SecurityUtils.getHashWithoutSalt(userProfile.getNewEmail() + ":"
    // + userProfile.getSalt());
    // if (serverNonce.equals(nonce)) {
    // userProfile.setEmail(userProfile.getNewEmail());
    // userProfile.setNewEmail(null);
    // result = true;
    // }
    // }
    // } else {
    // if (userProfile.getEmail().equals(email)) {
    // serverNonce = SecurityUtils.getHashWithoutSalt(userProfile.getEmail() + ":"
    // + userProfile.getSalt());
    // if (serverNonce.equals(nonce)) {
    // userProfile.setUserProfileStatus(UserProfileStatusType.active);
    // result = true;
    // }
    // }
    // }
    // userProfile.setNonce(null);
    // userProfileDAO.merge(userProfile);
    // }
    // }
    // return result;
    // }

    // @Override
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // public UserDTO validateSession() throws Exception {
    // // attempt to login user from sessionId cookie. if not, look for
    // // remember me cookie.
    //
    // // TODO according to
    // // http://code.google.com/p/google-web-toolkit-incubator/wiki/LoginSecurityFAQ
    // // this might not be completely secure
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "validateSession: ");
    // UserDTO result = null;
    // String sessionId = findCookieValue(Constants.SESSION_ID_COOKIE_NAME);
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "sessionId: " + sessionId);
    // String serverSessionId = (String) SessionUtils.getAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME);
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "serverSessionId: " + serverSessionId);
    // if (Utils.hasText(serverSessionId) && serverSessionId.equals(sessionId)) {
    // UserProfile userProfile = userProfileDAO.findByField("session_id", sessionId);
    // if (userProfile != null) {
    // UserDTO userDTO = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile, UserDTO.class);
    // UserProfileDTO userProfileDTO = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile,
    // UserProfileDTO.class);
    // Content descriptionContent = contentDAO.findContentForLocale(userProfile.getDescriptionContentId(),
    // ContentKindType.user_description, SessionUtils.getUserLocale());
    // if (descriptionContent != null) {
    // ContentDTO descriptionContentDTO = DozerBeanMapperSingletonWrapper.getInstance().map(
    // descriptionContent, ContentDTO.class);
    // userDTO.setDescriptionContent(descriptionContentDTO);
    // userProfileDTO.setDescriptionContent(descriptionContentDTO);
    // }
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "validateSession, user found from sessionId cookie.");
    // SessionUtils.setAttribute(Constants.USER_PROFILE_ATTRIBUTE_NAME, userProfileDTO);
    // result = userDTO;
    // }
    // }
    // if (result == null) {
    // String rememberMe = findCookieValue(Constants.REMEMBER_ME_COOKIE_NAME);
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "rememberMe: " + rememberMe);
    // if (Utils.hasText(rememberMe)) {
    // String rememberMeCompleteDecoded = new String(Base64.decodeBase64(rememberMe), "UTF-8");
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "rememberMeCompleteDecoded: " + rememberMeCompleteDecoded);
    // String[] components = rememberMeCompleteDecoded.split(":");
    // if (components != null && components.length == 3) {
    // UserProfile userProfile = userProfileDAO.findByField("username", components[0]);
    // if (userProfile != null) {
    // String rememberMeHashDecoded = SecurityUtils.getHashWithoutSalt(components[0] + ":"
    // + components[1] + ":" + userProfile.getSalt());
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL, "rememberMeHashDecoded: " + rememberMeHashDecoded);
    // if (Utils.hasText(rememberMeHashDecoded) && rememberMeHashDecoded.equals(components[2])) {
    // logger.log(Constants.SERVER_DEBUG_LOG_LEVEL,
    // "validateSession, user verified from rememberMe cookie.");
    //
    // userProfile.setSessionId(SessionUtils.getSessionId());
    // userProfile.setLastLoginDate(new Date());
    // userProfile.setLastIp(SessionUtils.getRemoteHost());
    //
    // userProfileDAO.merge(userProfile);
    //
    // UserDTO userDTO = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile,
    // UserDTO.class);
    // UserProfileDTO userProfileDTO = DozerBeanMapperSingletonWrapper.getInstance().map(
    // userProfile, UserProfileDTO.class);
    // Content descriptionContent = contentDAO.findContentForLocale(
    // userProfile.getDescriptionContentId(), ContentKindType.user_description,
    // SessionUtils.getUserLocale());
    // if (descriptionContent != null) {
    // ContentDTO descriptionContentDTO = DozerBeanMapperSingletonWrapper.getInstance().map(
    // descriptionContent, ContentDTO.class);
    // userDTO.setDescriptionContent(descriptionContentDTO);
    // userProfileDTO.setDescriptionContent(descriptionContentDTO);
    // }
    // SessionUtils.setAttribute(Constants.USER_PROFILE_ATTRIBUTE_NAME, userProfileDTO);
    // SessionUtils.setAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME,
    // userProfileDTO.getSessionId());
    // result = userDTO;
    // }
    // }
    // }
    // }
    // }
    // // TODO probably, in case of result != null do similar thing like in
    // // login, update last ip and login date, save session cookie and session
    // // attribute
    // return result;
    // }

    // private String findCookieValue(String cookieName) {
    // String result = null;
    // Cookie[] cookies = SessionUtils.getCookies();
    // if ((cookies != null) && (cookies.length > 0)) {
    // for (int i = 0; i < cookies.length; i++) {
    // if (cookies[i].getName().equals(cookieName)) {
    // result = cookies[i].getValue();
    // break;
    // }
    // }
    // }
    // return result;
    // }

    // @Override
    // public UserDTO findUserByUsername(String username) {
    // UserDTO result = null;
    // UserProfile userProfile = userProfileDAO.findByField("username", username);
    // if (userProfile != null) {
    // UserDTO userDTO = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile, UserDTO.class);
    // Content descriptionContent = contentDAO.findContentForLocale(userProfile.getDescriptionContentId(),
    // ContentKindType.user_description, SessionUtils.getUserLocale());
    // if (descriptionContent != null) {
    // userDTO.setDescriptionContent(DozerBeanMapperSingletonWrapper.getInstance().map(descriptionContent,
    // ContentDTO.class));
    // }
    // result = userDTO;
    // }
    // return result;
    // }

    // @Override
    // public List<UserDTO> findItemListUsers(String query, Integer start, Integer end) throws Exception {
    // List<UserProfile> userProfiles = userProfileDAO.findItemListUsers(query, start, end);
    // List<UserDTO> userDTOs = new ArrayList<UserDTO>();
    // if (userProfiles != null) {
    // for (UserProfile userProfile : userProfiles) {
    // UserDTO userDTO = DozerBeanMapperSingletonWrapper.getInstance().map(userProfile, UserDTO.class);
    // Content descriptionContent = contentDAO.findContentForLocale(userProfile.getDescriptionContentId(),
    // ContentKindType.user_description, SessionUtils.getUserLocale());
    // if (descriptionContent != null) {
    // userDTO.setDescriptionContent(DozerBeanMapperSingletonWrapper.getInstance().map(descriptionContent,
    // ContentDTO.class));
    // }
    // userDTOs.add(userDTO);
    // }
    // }
    // return userDTOs;
    // }

    // @Override
    // public Integer findItemListUsersCount(String query) throws Exception {
    // return userProfileDAO.findItemListUsersCount(query);
    // }

    @Override
    public Boolean sendMessage(String title, String text, String recipientUsername) throws Exception {
	UserProfile recipientUserProfile = userProfileDAO.findByField("username", recipientUsername);
	if (recipientUserProfile != null) {
	    // TODO i18n
	    String emailSubject = "bundolo user " + SessionUtils.getUsername() + " sent you a message: " + title;
	    String emailBody = "Message text:\n" + text + "\n\nPlease use bundolo to reply!";
	    MailingUtils mailingUtils = (MailingUtils) applicationContext.getBean("mailingUtils");
	    mailingUtils.sendEmail(emailBody, emailSubject, recipientUserProfile.getEmail());
	    return true;
	}
	return false;
    }

    @Override
    public Boolean sendNewPassword(String email) throws Exception {
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
	    MailingUtils mailingUtils = (MailingUtils) applicationContext.getBean("mailingUtils");
	    mailingUtils.sendEmail(emailBody, emailSubject, recipientUserProfile.getEmail());
	    return true;
	}
	return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	this.applicationContext = applicationContext;
    }

    @Override
    public List<UserProfile> findUserProfiles(Integer start, Integer end, String[] orderBy, String[] order,
	    String[] filterBy, String[] filter) {
	return userProfileDAO.findUserProfiles(start, end, orderBy, order, filterBy, filter);
    }

}
