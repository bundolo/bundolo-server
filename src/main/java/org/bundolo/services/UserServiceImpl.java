package org.bundolo.services;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bundolo.dao.UserDAO;
import org.bundolo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserProfileServiceImpl.class.getName());

    @Autowired
    private UserDAO userDAO;

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

}
