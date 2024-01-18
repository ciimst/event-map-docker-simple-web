package com.imst.event.map.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SecurityController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginShow(Model model) {
		
		return "login";
	}
	
	@RequestMapping(value = "/keycloaklogout", method = RequestMethod.GET)
	public String logoutShow(Model model) {
		
		return "logout";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginCheck() {
		
		return "page/home";
	}
	
}
