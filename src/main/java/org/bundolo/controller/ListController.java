package org.bundolo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bundolo.Constants;
import org.bundolo.model.Comment;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.Contest;
import org.bundolo.model.User;
import org.bundolo.model.enumeration.AnnouncementColumnType;
import org.bundolo.model.enumeration.AuthorColumnType;
import org.bundolo.model.enumeration.ColumnDataType;
import org.bundolo.model.enumeration.CommentColumnType;
import org.bundolo.model.enumeration.ConnectionColumnType;
import org.bundolo.model.enumeration.ContestColumnType;
import org.bundolo.model.enumeration.EpisodeColumnType;
import org.bundolo.model.enumeration.SerialColumnType;
import org.bundolo.model.enumeration.TextColumnType;
import org.bundolo.model.enumeration.TopicColumnType;
import org.bundolo.services.CommentService;
import org.bundolo.services.ConnectionService;
import org.bundolo.services.ContentService;
import org.bundolo.services.ContestService;
import org.bundolo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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

    @RequestMapping(value = Constants.REST_PATH_CONNECTIONS, method = RequestMethod.GET)
    public @ResponseBody
    List<Connection> connections(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(ConnectionColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_CONTESTS, method = RequestMethod.GET)
    public @ResponseBody
    List<Contest> contests(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(ContestColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_AUTHORS, method = RequestMethod.GET)
    public @ResponseBody
    List<User> authors(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(AuthorColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_TEXTS, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> texts(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(TextColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_ANNOUNCEMENTS, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> announcements(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(AnnouncementColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_SERIALS, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> serials(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(SerialColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_TOPICS, method = RequestMethod.GET)
    public @ResponseBody
    List<Content> topics(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(TopicColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_COMMENTS, method = RequestMethod.GET)
    public @ResponseBody
    List<Comment> comments(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(CommentColumnType.valueOf(params[i]).getColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
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

    @RequestMapping(value = Constants.REST_PATH_NEXT, method = RequestMethod.GET)
    public @ResponseBody
    Object next(@RequestParam(required = true) String type, @RequestParam(required = true) String id,
	    @RequestParam(required = false, defaultValue = "creationDate") String orderBy,
	    @RequestParam(required = false) String fixBy,
	    @RequestParam(required = false, defaultValue = "true") Boolean ascending) {
	logger.log(Level.WARNING, "next, type: " + type + ", id: " + id + ", orderBy: " + orderBy + ", ascending: "
		+ ascending);
	Object result = null;
	// TODO check param validity
	switch (type) {
	case "connection":
	    result = connectionService.findNext(Long.valueOf(id),
		    ConnectionColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? ConnectionColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "contest":
	    result = contestService.findNext(Long.valueOf(id), ContestColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? ContestColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "author":
	    result = userService.findNext(id, AuthorColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? AuthorColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "announcement":
	    result = contentService.findNext(Long.valueOf(id), AnnouncementColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? AnnouncementColumnType.valueOf(fixBy).getColumnName() : null,
		    ascending);
	    break;
	case "topic":
	    result = contentService.findNext(Long.valueOf(id), TopicColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? TopicColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "text":
	    result = contentService.findNext(Long.valueOf(id), TextColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? TextColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "serial":
	    result = contentService.findNext(Long.valueOf(id), SerialColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? SerialColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	case "episode":
	    result = contentService.findNext(Long.valueOf(id), EpisodeColumnType.valueOf(orderBy).getColumnName(),
		    StringUtils.hasText(fixBy) ? EpisodeColumnType.valueOf(fixBy).getColumnName() : null, ascending);
	    break;
	}
	logger.log(Level.WARNING, "next, result: " + result);
	return result;
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

}