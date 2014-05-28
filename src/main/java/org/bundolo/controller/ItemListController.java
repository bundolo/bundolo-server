package org.bundolo.controller;

import org.bundolo.model.Connection;
import org.bundolo.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemListController {

    @Autowired
    private ConnectionService connectionService;

    @RequestMapping("/kiloster")
    public @ResponseBody
    Connection greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
	// return new Greeting(counter.incrementAndGet(),
	// String.format(template, name));
	return connectionService.findConnection(1L);
    }
}