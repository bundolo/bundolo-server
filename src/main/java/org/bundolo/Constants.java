package org.bundolo;

public class Constants {

    public static final String DEFAULT_LOCALE = "sr";
    public static final long DEFAULT_RATING_INCREMENT = 1l;
    public static final long DEFAULT_PERSONAL_RATING = 0l;
    public static final long MAX_PERSONAL_RATING = 3l;
    public static final long MIN_PERSONAL_RATING = -3l;

    // TODO i18n
    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy.";

    public static final String DEFAULT_GUEST_SLUG = "author/gost";

    public static final String REST_PATH_TEXTS = "/texts";
    public static final String REST_PATH_TEXT = "/text";
    public static final String REST_PATH_SERIALS = "/serials";
    public static final String REST_PATH_SERIAL = "/serial";
    public static final String REST_PATH_EPISODES = "/episodes";
    public static final String REST_PATH_EPISODE = "/episode";
    public static final String REST_PATH_AUTHORS = "/authors";
    public static final String REST_PATH_AUTHOR = "/author";
    public static final String REST_PATH_ANNOUNCEMENTS = "/announcements";
    public static final String REST_PATH_ANNOUNCEMENT = "/announcement";
    public static final String REST_PATH_TOPICS = "/topics";
    public static final String REST_PATH_TOPIC = "/topic";
    public static final String REST_PATH_TOPIC_GROUPS = "/topic_groups";
    public static final String REST_PATH_POSTS = "/posts";
    public static final String REST_PATH_POST = "/post";
    public static final String REST_PATH_CONTESTS = "/contests";
    public static final String REST_PATH_CONTEST = "/contest";
    public static final String REST_PATH_CONNECTIONS = "/connections";
    public static final String REST_PATH_CONNECTION = "/connection";
    public static final String REST_PATH_CONNECTION_GROUPS = "/connection_groups";
    public static final String REST_PATH_PARENT_COMMENTS = "/parent_comments";
    public static final String REST_PATH_COMMENTS = "/comments";
    public static final String REST_PATH_COMMENT = "/comment";
    public static final String REST_PATH_PAGE = "/page";
    public static final String REST_PATH_AUTH = "/auth";
    public static final String REST_PATH_PASSWORD = "/password";
    public static final String REST_PATH_VALIDATE = "/validate";
    public static final String REST_PATH_AUTHOR_ITEMS = "/author_items";
    public static final String REST_PATH_USER_ITEMS = "/user_items";
    public static final String REST_PATH_NEXT = "/next";
    public static final String REST_PATH_MESSAGE = "/message";
    public static final String REST_PATH_RECENT = "/recent";
    public static final String REST_PATH_RATING = "/rating";
    public static final String REST_PATH_ITEM_LISTS = "/item_lists";
    public static final String REST_PATH_ITEM_LIST = "/item_list";
    public static final String REST_PATH_METRICS = "/metrics";

    // TODO rework validation
    // public static final String URL_SAFE_REGEX = "^[^~\\\\/\\[\\]\\{\\}\\(\\);\\:\\\"\\\'\\|<>\\?\\+=`#$%\\^&\\*]+$";
    // public static final String USERNAME_SAFE_REGEX = "^[A-Za-z0-9_-]{3,25}$";

    public static final int DEFAULT_PASSWORD_LENGTH = 8;

    public static final int NEWSLETTER_SENDER_INTERVAL = 5000;

    public static final String BOT_REQUEST_SUFFIX = "?_escaped_fragment_=";
    public static final String BOT_REQUEST_SUFFIX_ESCAPED = "?_escaped_fragment_=";

    public static final int SLUG_MAX_LENGTH = 255;
}