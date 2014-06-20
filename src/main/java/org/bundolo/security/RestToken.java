package org.bundolo.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class RestToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;

    public RestToken(String key, String credentials) {
	super(key, credentials);
    }

    public RestToken(String key, String credentials, Collection<? extends GrantedAuthority> authorities) {
	super(key, credentials, authorities);
    }

    public String getKey() {
	return (String) super.getPrincipal();
    }

    @Override
    public String getCredentials() {
	return (String) super.getCredentials();
    }
}
