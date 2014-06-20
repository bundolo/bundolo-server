package org.bundolo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

public class RestAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(RestAuthenticationProvider.class.getName());

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	// logger.log(Level.WARNING, "authentication: " + authentication);
	RestToken restToken = (RestToken) authentication;

	String key = restToken.getKey();
	String credentials = restToken.getCredentials();

	// if (!key.equals("jack") || !credentials.equals("jill")) {
	// throw new BadCredentialsException("Enter a username and password");
	// }
	// TODO go to database here, find user, assign ROLE_USER. see about ROLE_OWNER.

	return getAuthenticatedUser(key, credentials);
    }

    private Authentication getAuthenticatedUser(String key, String credentials) {
	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	// authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));

	return new RestToken(key, credentials, authorities);
    }

    @Override
    /*
        Determines if this class can support the token provided by the filter.
     */
    public boolean supports(Class<?> authentication) {
	return RestToken.class.equals(authentication);
    }
}
