package org.bundolo.controller;

import java.util.ArrayList;
import java.util.List;

import org.bundolo.Constants;
import org.bundolo.model.Connection;
import org.bundolo.model.Content;
import org.bundolo.model.Contest;
import org.bundolo.model.UserProfile;
import org.bundolo.model.enumeration.ConnectionColumnType;
import org.bundolo.model.enumeration.ContestColumnType;
import org.bundolo.model.enumeration.TextColumnType;
import org.bundolo.model.enumeration.UserProfileColumnType;
import org.bundolo.services.ConnectionService;
import org.bundolo.services.ContentService;
import org.bundolo.services.ContestService;
import org.bundolo.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ContentService contentService;

    @RequestMapping(Constants.REST_PATH_CONNECTIONS)
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
		orderByColumns.add(ConnectionColumnType.valueOf(params[i]).getConnectionColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		filterByColumns.add(ConnectionColumnType.valueOf(params[i]).getConnectionColumnName());
		filterByTexts.add(params[i + 1]);
	    }
	}
	return connectionService.findConnections(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(Constants.REST_PATH_CONTESTS)
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
		orderByColumns.add(ContestColumnType.valueOf(params[i]).getContestColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		filterByColumns.add(ContestColumnType.valueOf(params[i]).getContestColumnName());
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contestService.findContests(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(Constants.REST_PATH_AUTHORS)
    public @ResponseBody
    List<UserProfile> authors(@RequestParam(required = false, defaultValue = "0") Integer start,
	    @RequestParam(required = false, defaultValue = "0") Integer end,
	    @RequestParam(required = false) String orderBy, @RequestParam(required = false) String filterBy) {
	// TODO check param validity
	List<String> orderByColumns = new ArrayList<String>();
	List<String> orderByDirections = new ArrayList<String>();
	if (StringUtils.hasText(orderBy)) {
	    String[] params = orderBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		orderByColumns.add(UserProfileColumnType.valueOf(params[i]).getUserProfileColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		filterByColumns.add(UserProfileColumnType.valueOf(params[i]).getUserProfileColumnName());
		filterByTexts.add(params[i + 1]);
	    }
	}
	return userProfileService.findUserProfiles(start, end,
		orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }

    @RequestMapping(Constants.REST_PATH_TEXTS)
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
		orderByColumns.add(TextColumnType.valueOf(params[i]).getTextColumnName());
		orderByDirections.add(params[i + 1]);
	    }
	}
	List<String> filterByColumns = new ArrayList<String>();
	List<String> filterByTexts = new ArrayList<String>();
	if (StringUtils.hasText(filterBy)) {
	    String[] params = filterBy.split(",");
	    for (int i = 0; i < params.length; i += 2) {
		filterByColumns.add(TextColumnType.valueOf(params[i]).getTextColumnName());
		filterByTexts.add(params[i + 1]);
	    }
	}
	return contentService.findTexts(start, end, orderByColumns.toArray(new String[orderByColumns.size()]),
		orderByDirections.toArray(new String[orderByDirections.size()]),
		filterByColumns.toArray(new String[filterByColumns.size()]),
		filterByTexts.toArray(new String[filterByTexts.size()]));
    }
}