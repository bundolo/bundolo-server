package org.bundolo.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

public class CustomRestSecurityFilter extends GenericFilterBean {

	private static final Logger logger = Logger.getLogger(CustomRestSecurityFilter.class.getName());

	private final AuthenticationManager authenticationManager;
	private final AuthenticationEntryPoint authenticationEntryPoint;

	public CustomRestSecurityFilter(AuthenticationManager authenticationManager) {
		this(authenticationManager, new BasicAuthenticationEntryPoint());
		((BasicAuthenticationEntryPoint) authenticationEntryPoint).setRealmName("Not authorized");
	}

	public CustomRestSecurityFilter(AuthenticationManager authenticationManager,
			AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationManager = authenticationManager;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		// Pull out the Authorization header
		String authorization = request.getHeader("Authorization");

		// if authotization header is null, proceed with authorisation to get
		// UsernamePasswordAuthenticationToken
		// without authorities
		String[] credentials = { "", "" };
		if (authorization != null) {
			credentials = decodeHeader(authorization);
		}
		if (credentials.length == 2) {
			// logger.log(Level.WARNING, "credentials: " + credentials);
			Authentication authentication = new UsernamePasswordAuthenticationToken(credentials[0], credentials[1]);

			try {
				// Request the authentication manager to authenticate the token
				Authentication successfulAuthentication = authenticationManager.authenticate(authentication);
				// Pass the successful token to the SecurityHolder where it can
				// be retrieved by this thread at any
				// stage.
				SecurityContextHolder.getContext().setAuthentication(successfulAuthentication);
				// Continue with the Filters
				chain.doFilter(request, response);
			} catch (AuthenticationException authenticationException) {
				// If it fails clear this threads context and kick off the
				// authentication entry point process.
				SecurityContextHolder.clearContext();
				authenticationEntryPoint.commence(request, response, authenticationException);
			}
		}
	}

	public String[] decodeHeader(String authorization) {
		// Decode the Auth Header to get the username and password
		try {
			byte[] decoded = Base64.decode(authorization.substring(6).getBytes("UTF-8"));
			String credentials = new String(decoded);
			return credentials.split(":");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
