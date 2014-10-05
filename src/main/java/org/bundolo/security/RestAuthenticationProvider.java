package org.bundolo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.UserProfileDAO;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.UserProfileStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class RestAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(RestAuthenticationProvider.class.getName());

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Override
    // @Transactional(propagation = Propagation.NEVER, rollbackFor = Exception.class)
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	// logger.log(Level.WARNING, "authenticate authentication: " + authentication);
	UsernamePasswordAuthenticationToken restToken = (UsernamePasswordAuthenticationToken) authentication;

	String key = (String) restToken.getPrincipal();
	String credentials = (String) restToken.getCredentials();
	// TODO consider performance here, we do db query even if user did not provide username and password
	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	String proposedUsername;
	String proposedPassword;
	if (StringUtils.isBlank(key)) {
	    proposedUsername = Constants.DEFAULT_GUEST_USERNAME;
	} else {
	    proposedUsername = key;
	}
	if (StringUtils.isBlank(credentials)) {
	    proposedPassword = " ";
	} else {
	    proposedPassword = credentials;
	}
	UserProfile userProfile = userProfileDAO.findByField("username", proposedUsername);
	String dbSalt;
	String dbPassword;
	UserProfileStatusType dbStatus;
	if (userProfile != null) {
	    dbSalt = userProfile.getSalt();
	    dbPassword = userProfile.getPassword();
	    dbStatus = userProfile.getUserProfileStatus();
	} else {
	    dbSalt = "";
	    dbPassword = "";
	    dbStatus = UserProfileStatusType.disabled;
	}
	boolean credentialsValid;
	try {
	    credentialsValid = SecurityUtils.getHashWithPredefinedSalt(proposedPassword, dbSalt).equals(dbPassword)
		    && (UserProfileStatusType.active.equals(dbStatus));
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "authenticateUser exception: " + ex);
	    credentialsValid = false;
	}
	if (credentialsValid) {
	    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	} else {
	    authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
	}
	return new UsernamePasswordAuthenticationToken(key, credentials, authorities);
    }

    @Override
    /*
        Determines if this class can support the token provided by the filter.
     */
    public boolean supports(Class<?> authentication) {
	return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
