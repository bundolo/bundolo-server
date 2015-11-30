package org.bundolo.services;

import java.util.List;

import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ReturnMessageType;
import org.springframework.http.ResponseEntity;

public interface UserService {

    public User findUser(String slug);

    // this method is only for back end, rating and last activity should not be updated
    public User findUserByUsername(String username);

    public List<User> findUsers(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public ReturnMessageType authenticateUser(String slug, String password);

    public ReturnMessageType activateUserEmailAddress(String email, String nonce);

    public ReturnMessageType sendMessage(String title, String text, String slug);

    public ReturnMessageType sendNewPassword(String slug, String email);

    public ResponseEntity<String> saveOrUpdateUser(UserProfile userProfile);

    public User findNext(Long userId, String orderBy, String fixBy, boolean ascending);

    public void clearSession();

    public Long deleteUser(String slug);

}