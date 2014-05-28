package org.bundolo.services;

import java.util.List;

import org.bundolo.model.User;
import org.bundolo.model.UserProfile;

public interface UserProfileService {

    // TODO this method should not be public. users on client side should be in
    // UserDTO object
    public UserProfile findUserProfile(Long userId);

    public void saveUserProfile(UserProfile userProfile) throws Exception;

    // public UserDTO updateUserProfile(UserProfileDTO userProfileDTO) throws
    // Exception;
    public User updateUserProfile(UserProfile userProfile) throws Exception;

    // public void saveOrUpdateUserProfile(UserProfileDTO userProfileDTO) throws
    // Exception;
    public void deleteUserProfile(Long userId) throws Exception;

    public User login(String username, String password, Boolean rememberMe) throws Exception;

    public User validateSession() throws Exception;

    public void logout() throws Exception;

    public Boolean activateUserProfileEmailAddress(String email, String nonce) throws Exception;

    public User findUserByUsername(String username);

    public List<User> findItemListUsers(String query, Integer start, Integer end) throws Exception;

    public Integer findItemListUsersCount(String query) throws Exception;

    public Boolean sendMessage(String title, String text, String recipientUsername) throws Exception;

    public Boolean sendNewPassword(String email) throws Exception;
}