package com.imst.event.map.web.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
public class GenericResponseItem {
	
	private boolean state;
	private String description;
	private String redirectUrl;
	private Object data;
	
	public GenericResponseItem(boolean state, String description) {
		this.state = state;
		this.description = description;
		if (!state) {
			log.debug(description);
		}
	}
	
	public GenericResponseItem(boolean state, String description, String redirectUrl) {
		this(state, description);
		this.redirectUrl = redirectUrl;
	}
	
}
