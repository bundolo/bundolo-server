package org.bundolo.controller;

import org.bundolo.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

}