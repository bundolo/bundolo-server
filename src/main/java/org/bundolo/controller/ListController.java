package org.bundolo.controller;

import java.util.ArrayList;
import java.util.List;

import org.bundolo.Constants;
import org.bundolo.model.Connection;
import org.bundolo.model.enumeration.ConnectionColumnType;
import org.bundolo.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {

    // private final String[] a = { 1, 2, 3, 4, 5 };

    @Autowired
    private ConnectionService connectionService;

    @RequestMapping(Constants.REST_PATH_CONNECTIONS)
    public @ResponseBody
    List<Connection> connections(@RequestParam Integer start, @RequestParam Integer end, @RequestParam String orderBy,
	    @RequestParam String filterBy) {
	// TODO check param validity
	if (start == null) {
	    start = 0;
	}
	if (end == null) {
	    end = 0;
	}
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
}