package org.bundolo;

import java.util.logging.Level;

public class Constants {

    public static final Level SERVER_SEVERE_LOG_LEVEL = Level.SEVERE;
    public static final Level SERVER_WARN_LOG_LEVEL = Level.WARNING;
    public static final Level SERVER_INFO_LOG_LEVEL = Level.INFO;
    public static final Level SERVER_DEBUG_LOG_LEVEL = Level.FINE;

    public static final String DEFAULT_LOCALE = "sr";
    public static final int DEFAULT_PAGING_PAGE_SIZE = 20;
    public static final int DETAIL_PAGING_PAGE_SIZE = 10;

    public static final String BUNDOLO_EMAIL_ADDRESS = "daniel.farkas0@gmail.com";
    public static final String BUNDOLO_FACEBOOK = "https://www.facebook.com/bundolo.dibidus";

    // TODO to be localized
    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy.";
    // public static final DateTimeFormat DEFAULT_DATE_FORMAT =
    // DateTimeFormat.getFormat(DEFAULT_DATE_FORMAT_STRING);

    public static final String SESSION_ID_COOKIE_NAME = "sessionId";
    public static final String REMEMBER_ME_COOKIE_NAME = "rememberMe";
    public static final String SESSION_ID_ATTRIBUTE_NAME = "sessionId";
    public static final String USER_PROFILE_ATTRIBUTE_NAME = "userProfileDTO";
    public static final String LOCALE_ATTRIBUTE_NAME = "locale";

    // TODO to be removed once db is updated
    public static final String OLD_DB_GUEST_USERNAME = "gost";

}
