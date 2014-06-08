package org.bundolo.services;

import java.util.List;

import org.bundolo.model.User;

public interface UserService {

    public User findUser(String username);

    public List<User> findUsers(Integer start, Integer end, String[] orderBy, String[] order, String[] filterBy,
	    String[] filter);

}