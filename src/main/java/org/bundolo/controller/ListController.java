package org.bundolo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.bundolo.Constants;
import org.bundolo.SecurityUtils;
import org.bundolo.dao.UserProfileDAO;
import org.bundolo.model.Comment;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.Contest;
import org.bundolo.model.ItemList;
import org.bundolo.model.User;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.AnnouncementColumnType;
import org.bundolo.model.enumeration.AuthorColumnType;
import org.bundolo.model.enumeration.AuthorInteractionsColumnType;
import org.bundolo.model.enumeration.AuthorItemsColumnType;
import org.bundolo.model.enumeration.ColumnDataType;
import org.bundolo.model.enumeration.CommentColumnType;
import org.bundolo.model.enumeration.ConnectionColumnType;
import org.bundolo.model.enumeration.ContentKindType;
import org.bundolo.model.enumeration.ContestColumnType;
import org.bundolo.model.enumeration.EpisodeColumnType;
import org.bundolo.model.enumeration.ItemListColumnType;
import org.bundolo.model.enumeration.ItemListItemsColumnType;
import org.bundolo.model.enumeration.SerialColumnType;
import org.bundolo.model.enumeration.TextColumnType;
import org.bundolo.model.enumeration.TopicColumnType;
import org.bundolo.services.CommentService;
import org.bundolo.services.ConnectionService;
import org.bundolo.services.ContentService;
import org.bundolo.services.ContestService;
import org.bundolo.services.ItemListService;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {

    private static final Logger logger = Logger.getLogger(ListController.class.getName());

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ItemListService itemListService;

    @Autowired
    private UserProfileDAO userProfileDAO;

    // TODO investigate returning ResponseEntity<List<Connection>> instead of List<Connection>
    @RequestMapping(value = { Constants.REST_PATH_CONNECTIONS,
	    Constants.REST_PATH_CONNECTIONS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_CONNECTIONS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Connection> connections(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(ConnectionColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		ConnectionColumnType connectionColumnType = ConnectionColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(connectionColumnType.getColumnName(),
			connectionColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return connectionService.findConnections(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_CONTESTS,
	    Constants.REST_PATH_CONTESTS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_CONTESTS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Contest> contests(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(ContestColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		ContestColumnType contestColumnType = ContestColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(contestColumnType.getColumnName(),
			contestColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contestService.findContests(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_AUTHORS, Constants.REST_PATH_AUTHORS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_AUTHORS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<User> authors(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(AuthorColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		AuthorColumnType authorColumnType = AuthorColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(authorColumnType.getColumnName(),
			authorColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return userService.findUsers(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_TEXTS, Constants.REST_PATH_TEXTS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_TEXTS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> texts(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(TextColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		TextColumnType textColumnType = TextColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(textColumnType.getColumnName(),
			textColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contentService.findTexts(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_ANNOUNCEMENTS,
	    Constants.REST_PATH_ANNOUNCEMENTS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_ANNOUNCEMENTS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> announcements(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(AnnouncementColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		AnnouncementColumnType announcementColumnType = AnnouncementColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(announcementColumnType.getColumnName(),
			announcementColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contentService.findAnnouncements(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_SERIALS, Constants.REST_PATH_SERIALS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_SERIALS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> serials(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(SerialColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		SerialColumnType serialColumnType = SerialColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(serialColumnType.getColumnName(),
			serialColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contentService.findSerials(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_TOPICS, Constants.REST_PATH_TOPICS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_TOPICS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> topics(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(TopicColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		TopicColumnType topicColumnType = TopicColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(topicColumnType.getColumnName(),
			topicColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contentService.findTopics(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_COMMENTS,
	    Constants.REST_PATH_COMMENTS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_COMMENTS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Comment> comments(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(CommentColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		CommentColumnType commentColumnType = CommentColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(commentColumnType.getColumnName(),
			commentColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return commentService.findComments(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_ITEM_LISTS,
	    Constants.REST_PATH_ITEM_LISTS + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_ITEM_LISTS + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<ItemList> itemLists(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(ItemListColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		ItemListColumnType itemListColumnType = ItemListColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(itemListColumnType.getColumnName(),
			itemListColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return itemListService.findItemLists(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_NEXT, Constants.REST_PATH_NEXT + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_NEXT + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    Object next(@RequestParam(required = true) String type, @RequestParam(required = true) String id,
	    @RequestParam(required = false, defaultValue = "creationDate") String orderBy,
	    @RequestParam(required = false) String fixBy,
	    @RequestParam(required = false, defaultValue = "true") Boolean ascending) {
	logger.log(Level.INFO, "next, type: " + type + ", id: " + id + ", orderBy: " + orderBy + ", ascending: "
		+ ascending);
	Object result = null;
	switch (type) {
	case "connection":
	    result = connectionService.findNext(Long.valueOf(id),
		    ConnectionColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? ConnectionColumnType.valueOf(fixBy).getColumnName() : null,
		    ascending);
	    break;
	case "contest":
	    result = contestService.findNext(Long.valueOf(id), ContestColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? ContestColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "author":
	    result = userService.findNext(id, AuthorColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? AuthorColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "announcement":
	    result = contentService.findNext(Long.valueOf(id), AnnouncementColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? AnnouncementColumnType.valueOf(fixBy).getColumnName() : null,
		    ascending);
	    break;
	case "topic":
	    result = contentService.findNext(Long.valueOf(id), TopicColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? TopicColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "text":
	    result = contentService.findNext(Long.valueOf(id), TextColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? TextColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "serial":
	    result = contentService.findNext(Long.valueOf(id), SerialColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? SerialColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "episode":
	    result = contentService.findNext(Long.valueOf(id), EpisodeColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.isNotBlank(fixBy) ? EpisodeColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	}
	// logger.log(Level.INFO, "next, result: " + result);
	return result;
    }

    @RequestMapping(value = { Constants.REST_PATH_RECENT, Constants.REST_PATH_RECENT + Constants.BOT_REQUEST_SUFFIX,
	    Constants.REST_PATH_RECENT + Constants.BOT_REQUEST_SUFFIX_ESCAPED }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> recent(@RequestParam(required = false) Date fromDate,
	    @RequestParam(required = false, defaultValue = "0") Integer limit) {
	// there is no paging of recent currently, if the performance becomes an issue, reconsider
	String senderUsername = SecurityUtils.getUsername();
	if (senderUsername != null && limit == 0) {
	    // if limit is not set, and user is known, set date to user previous activity
	    UserProfile userProfile = userProfileDAO.findByField("username", senderUsername);
	    if (userProfile != null) {
		fromDate = userProfile.getPreviousActivity();
	    }
	}
	return contentService.findRecent(fromDate, limit);
    }

    @RequestMapping(value = { Constants.REST_PATH_AUTHOR_ITEMS + "/" + Constants.REST_PATH_AUTHOR + "/{slug}",
	    Constants.REST_PATH_USER_ITEMS + "/" + Constants.REST_PATH_AUTHOR + "/{slug}" }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> authorItems(@PathVariable String slug,
	    @RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(AuthorItemsColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		AuthorItemsColumnType authorItemsColumnType = AuthorItemsColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(authorItemsColumnType.getColumnName(),
			authorItemsColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contentService.findAuthorItems(ContentKindType.user_description.getLocalizedName() + "/" + slug, start,
		end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_AUTHOR_INTERACTIONS + "/" + Constants.REST_PATH_AUTHOR + "/{slug}" }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> authorInteractions(@PathVariable String slug, @RequestParam(required = false) Date fromDate,
	    @RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	logger.log(Level.INFO, "authorInteractions, slug: " + slug + ", fromDate: " + fromDate + ", start: " + start
		+ ", end: " + end + ", orderBy: " + orderBy + ", filterBy: " + filterBy);
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(AuthorInteractionsColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		AuthorInteractionsColumnType authorInteractionsColumnType = AuthorInteractionsColumnType
			.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(authorInteractionsColumnType.getColumnName(),
			authorInteractionsColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	if (fromDate == null) {
	    String senderUsername = SecurityUtils.getUsername();
	    if (senderUsername != null) {
		// if user is known, fromDate should be set to previousActivity
		UserProfile userProfile = userProfileDAO.findByField("username", senderUsername);
		if (userProfile != null) {
		    fromDate = userProfile.getPreviousActivity();
		}
	    }
	    if (fromDate == null) {
		fromDate = new GregorianCalendar(1970, 0, 1).getTime();
	    }
	}
	return contentService.findAuthorInteractions(ContentKindType.user_description.getLocalizedName() + "/" + slug,
		fromDate, start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(value = { Constants.REST_PATH_ITEM_LIST_ITEMS + "/" + Constants.REST_PATH_ITEM_LIST + "/{slug}" }, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> itemListItems(@PathVariable String slug,
	    @RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	ItemList itemList = itemListService.findItemList(ContentKindType.item_list_description.getLocalizedName() + "/"
		+ slug);
	if (itemList == null) {
	    return null;
	}
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.isNotBlank(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(ItemListItemsColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(getOrderByDirection(params[i + 1]));
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.isNotBlank(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		ItemListItemsColumnType itemListItemsColumnType = ItemListItemsColumnType.valueOf(params[i]);
		filterByColumns.add(getFilterByColumn(itemListItemsColumnType.getColumnName(),
			itemListItemsColumnType.getColumnDataType()));
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contentService.findItemListItems(itemList.getQuery(), start, end,
		orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    private String getFilterByColumn(String columnName, ColumnDataType columnDataType) {
	switch (columnDataType) {
	case date:
	    return "to_char(" + columnName + ", 'DD.MM.YYYY.')";
	case text:
	    return columnName;
	case number:
	    return "cast(" + columnName + " as text)";
	default:
	    return columnName;
	}
    }

    private String getOrderByDirection(String direction) {
	if ("desc".equals(direction)) {
	    return "desc";
	} else {
	    return "asc";
	}
    }

}