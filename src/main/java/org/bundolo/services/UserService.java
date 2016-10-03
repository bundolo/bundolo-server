package org.bundolo.services;

import java.util.Date;
import java.util.List;

import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.springframework.http.ResponseEntity;

public interface UserService {

	public User findUser(String slug);

	public List<User> findUsers(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
			String[] filter);

	public ResponseEntity<String> authenticateUser(String username, String password);

	public ReturnMessageType activateUserEmailAddress(String email, String nonce);

	public ReturnMessageType sendMessage(String title, String text, String slug);

	public ReturnMessageType sendNewPassword(String username, String email);

	public ResponseEntity<String> saveOrUpdateUser(UserProfile userProfile);

	public User findNext(String username, String orderBy, String fixBy, boolean ascending);

	public void clearSession();

	public Long deleteUser(String slug);

	public void updateNewsletterSendingDate(List<UserProfile> users, Date newsletterSendingDate);

	public List<UserProfile> findNewsletterUsers(Date sendingStart, Date bulletinDate, Integer maxResults);

	public ReturnMessageType recommend(Long contentId, String recipientSlug);

}