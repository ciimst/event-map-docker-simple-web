package com.imst.event.map.web.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@RequestMapping("/")
public class LogController {
	
	@RequestMapping({"/logTest"})
	@ResponseBody
	public String logTest(Model model) {
		
		log.trace("trace log");
		log.debug("debug log");
		log.info("info log");
		log.warn("warn log");
		log.error("error log");
		log.fatal("fatal log");
		
				
		return "loglama bitti";
	}
}


