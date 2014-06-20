package org.bundolo.services;

import java.util.List;

import org.bundolo.model.User;

public interface UserService {

    public User findUser(String username);

    public List<User> findUsers(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

    public Boolean authenticateUser(String username, String password);

    public Boolean activateUserProfileEmailAddress(String email, String nonce);

    public Boolean sendMessage(String title, String text, String recipientUsername);

    public Boolean sendNewPassword(String email);

}