package org.bundolo;

import java.util.Date;
import java.util.List;

//import org.bundolo.client.LocalStorage.PresenterName;
import org.bundolo.model.enumeration.ContentKindType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

//import com.google.gwt.user.client.History;

public class Utils {

    private static final long MILLISECONDS_IN_SECOND = 1000l;
    private static final long SECONDS_IN_MINUTE = 60l;
    private static final long MINUTES_IN_HOUR = 60l;
    private static final long HOURS_IN_DAY = 24l;
    private static final long MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE;
    private static final long MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * MINUTES_IN_HOUR;
    private static final long MILLISECONDS_IN_DAY = MILLISECONDS_IN_HOUR * HOURS_IN_DAY;

    public static Date addMinutes(Date date, int minutes) {
	return new Date(date.getTime() + (minutes * MILLISECONDS_IN_MINUTE));
    }

    public static Date addHours(Date date, int hours) {
	return new Date(date.getTime() + (hours * MILLISECONDS_IN_HOUR));
    }

    public static Date addDays(Date date, int days) {
	return new Date(date.getTime() + (days * MILLISECONDS_IN_DAY));
    }

    public static boolean hasText(String text) {
	return text != null && text.length() > 0;
    }

    public static boolean hasElements(List<?> list) {
	return list != null && list.size() > 0;
    }

    public static boolean hasElements(Object[] array) {
	return array != null && array.length > 0;
    }

    public static ContentKindType getCommentContentKind(ContentKindType parentContentKind) {
	ContentKindType result = ContentKindType.page_comment;
	if (parentContentKind != null) {
	    switch (parentContentKind) {
	    case page_description:
	    case page_comment:
		result = ContentKindType.page_comment;
		break;
	    case text:
	    case text_comment:
		result = ContentKindType.text_comment;
		break;
	    case episode_group:
	    case episode_group_comment:
		result = ContentKindType.episode_group_comment;
		break;
	    case episode:
	    case episode_comment:
		result = ContentKindType.episode_comment;
		break;
	    case item_list_description:
	    case item_list_comment:
		result = ContentKindType.item_list_comment;
		break;
	    case connection_description:
	    case connection_comment:
		result = ContentKindType.connection_comment;
		break;
	    case news:
	    case news_comment:
		result = ContentKindType.news_comment;
		break;
	    case contest_description:
	    case contest_comment:
		result = ContentKindType.contest_comment;
		break;
	    case event:
	    case event_comment:
		result = ContentKindType.event_comment;
		break;
	    case label:
	    case label_comment:
		result = ContentKindType.label_comment;
		break;
	    case user_description:
	    case user_comment:
		result = ContentKindType.user_comment;
		break;
	    default:
		result = ContentKindType.page_comment;
		break;
	    }
	}
	return result;
    }

    public static ContentKindType getDescriptionContentKind(ContentKindType parentContentKind) {
	ContentKindType result = null;
	if (parentContentKind != null) {
	    switch (parentContentKind) {
	    case text:
		result = ContentKindType.text_description;
		break;
	    // serials have description instead of text, episodes didn't have
	    // descriptions in the
	    // old version
	    // case episode_group:
	    // result = ContentKindType.episode_group_description;
	    // break;
	    // case episode:
	    // result = ContentKindType.episode_description;
	    // break;
	    default:
		result = null;
		break;
	    }
	}
	return result;
    }

    // public static void setHistoryTokenIfNeeded(PresenterName presenterName) {
    // if (presenterName != null) {
    // setHistoryTokenIfNeeded(presenterName.name());
    // }
    // }
    //
    // public static void setHistoryTokenIfNeeded(String presenterName) {
    // if ((hasText(presenterName)) &&
    // (!presenterName.equalsIgnoreCase(History.getToken()))) {
    // History.newItem(presenterName);
    // }
    // }

    public static String format(String s, Object... arguments) {
	// A very simple implementation of format
	int i = 0;
	while (i < arguments.length) {
	    String delimiter = "{" + i + "}";
	    while (s.contains(delimiter)) {
		s = s.replace(delimiter, String.valueOf(arguments[i]));
	    }
	    i++;
	}
	return s;
    }

    public static String getUsername() {
	// TODO switch to using this to update rating
	// TODO cleanup SessionUtils, we don't keep user in session
	UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
		.getContext().getAuthentication();
	if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
	    return (String) authentication.getPrincipal();
	} else {
	    return null;
	}
    }
}
