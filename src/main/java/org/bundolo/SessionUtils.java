package org.bundolo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.bundolo.model.UserProfile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtils {

    public static String getUsername() {
	String result = null;
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	HttpSession session = sra.getRequest().getSession();
	UserProfile userProfile = (UserProfile) session.getAttribute(Constants.USER_PROFILE_ATTRIBUTE_NAME);
	if (userProfile != null) {
	    result = userProfile.getUsername();
	}
	return result;
    }

    public static String getUserLocale() {
	String result = null;
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	HttpSession session = sra.getRequest().getSession();
	result = (String) session.getAttribute(Constants.LOCALE_ATTRIBUTE_NAME);
	if (!Utils.hasText(result)) {
	    // TODO get default locale and set it in session
	}
	return result;
    }

    public static void setAttribute(String attributeName, Object attributeValue) {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	HttpSession session = sra.getRequest().getSession();
	session.setAttribute(attributeName, attributeValue);
    }

    public static String getSessionId() {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	HttpSession session = sra.getRequest().getSession();
	return session.getId();
    }

    public static Object getAttribute(String attributeName) {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	HttpSession session = sra.getRequest().getSession();
	return session.getAttribute(attributeName);
    }

    public static void removeAttribute(String attributeName) {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	HttpSession session = sra.getRequest().getSession();
	session.removeAttribute(attributeName);
    }

    public static Cookie[] getCookies() {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	return sra.getRequest().getCookies();
    }

    public static String getRemoteHost() {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	return sra.getRequest().getRemoteHost();
    }
}
