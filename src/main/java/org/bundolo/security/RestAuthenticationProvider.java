package org.bundolo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bundolo.dao.UserProfileDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

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

	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	if (StringUtils.hasText(key) && StringUtils.hasText(credentials)) {
	    String userPassword = userProfileDAO.findPassword(key);
	    if (StringUtils.hasText(userPassword) && userPassword.equals(credentials)) {
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	    }
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
