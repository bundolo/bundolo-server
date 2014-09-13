package org.bundolo.services;

import java.util.List;

import org.bundolo.model.User;
import org.bundolo.model.UserProfile;

public interface UserService {

    public User findUser(String username);

    public List<User> findUsers(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public Boolean authenticateUser(String username, String password);

    public Boolean activateUserEmailAddress(String email, String nonce);

    public Boolean sendMessage(String title, String text, String recipientUsername);

    public Boolean sendNewPassword(String username, String email);

    public Boolean saveOrUpdateUser(UserProfile userProfile);

    public User findNext(String username, String orderBy, String fixBy, boolean ascending);

    public void clearSession();

    public Long deleteUser(String username);

}