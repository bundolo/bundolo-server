package org.bundolo.controller;

import java.util.List;

import org.bundolo.model.Connection;
import org.bundolo.model.ItemList;
import org.bundolo.model.enumeration.ItemListNameType;
import org.bundolo.services.ConnectionService;
import org.bundolo.services.ItemListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ItemListService itemListService;

    @RequestMapping("/latest")
    public @ResponseBody
    List<Connection> latest() {
	// return new Greeting(counter.incrementAndGet(),
	// String.format(template, name));
	ItemList itemList = itemListService
		.findItemListByName(ItemListNameType.new_connections.getItemListName(), null);
	try {
	    return connectionService.findItemListConnections(itemList.getQuery(), 0, 4);
	} catch (Exception e) {
	    return null;
	}
    }
}